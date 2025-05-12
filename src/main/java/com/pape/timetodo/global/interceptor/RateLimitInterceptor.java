package com.pape.timetodo.global.interceptor;

import com.pape.timetodo.global.exception.AppException;
import com.pape.timetodo.global.exception.ExceptionCode;
import com.pape.timetodo.global.jpa.entity.LoggingEntity;
import com.pape.timetodo.global.jpa.entity.UsersEntity;
import com.pape.timetodo.global.jpa.repository.LoggingRepository;
import com.pape.timetodo.global.util.UserUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final LoggingRepository loggingRepository;
    private final UserUtil userUtil;

    // 요청 카운터 및 타임스탬프를 저장하는 ConcurrentHashMap
    private static final ConcurrentHashMap<String, RequestCounter> REQUEST_COUNT_MAP = new ConcurrentHashMap<>();

    // 기본 설정값
    private static final long DEFAULT_REQUEST_LIMIT = 50; // 기본 요청 제한 (분당)
    private static final int DEFAULT_WINDOW_SIZE_SECONDS = 60; // 기본 시간 윈도우 (초)

    // 만료된 카운터를 주기적으로 정리하는 스케줄러
    private static final ScheduledExecutorService CLEANUP_SCHEDULER = Executors.newSingleThreadScheduledExecutor();

    // 공개 API 목록 (IP 기반으로 제한)
    private static final Map<String, Boolean> PUBLIC_APIS = Map.of(
            "/api/v1/user/register", true,
            "/api/v1/user/login", true,
            "/api/v1/user/sns/login", true,
            "/api/v1/user/nickname/check", true,
            "/api/v1/mail/send/register", true,
            "/api/v1/mail/certification/register", true,
            "/api/v1/mail/send/finding", true,
            "/api/v1/mail/certification/id", true,
            "/api/v1/noti/receive/agree", true,
            "/api/v1/user/password", true
    );

    // 각 공개 API별 요청 제한값 설정 (이 외 API 요청의 경우, 기본 제한값 설정에 따라 Username 기반으로 제한)
    private static final Map<String, Long> API_RATE_LIMITS = Map.of(
            "/api/v1/user/register", 80L, //3L,
            "/api/v1/user/login", 80L, //5L,
            "/api/v1/user/sns/login", 80L, //5L,
            "/api/v1/user/nickname/check", 80L, //5L,
            "/api/v1/mail/send/register", 80L, //3L,
            "/api/v1/mail/certification/register", 80L, //3L,
            "/api/v1/mail/send/finding", 80L, //3L,
            "/api/v1/mail/certification/id", 80L, //3L,
            "/api/v1/noti/receive/agree", 80L, //5L,
            "/api/v1/user/password", 80L //3L
    );

    // 클래스 초기화 시 청소 스케줄러 시작
    static {
        CLEANUP_SCHEDULER.scheduleAtFixedRate(
                () -> {
                    LocalDateTime now = LocalDateTime.now();
                    REQUEST_COUNT_MAP.entrySet().removeIf(entry ->
                            entry.getValue().getLastRequestTime().plusSeconds(DEFAULT_WINDOW_SIZE_SECONDS).isBefore(now));
                    log.debug("Cleaned up expired rate limit counters. Current map size: {}", REQUEST_COUNT_MAP.size());
                },
                1, 1, TimeUnit.MINUTES);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        // API별 요청 제한 설정 가져오기 (설정이 없으면 기본값 사용)
        long requestLimit = API_RATE_LIMITS.getOrDefault(requestURI, DEFAULT_REQUEST_LIMIT);

        // 키 생성 (공개 API는 IP 기반, 인증 API는 username 기반)
        String key;
        if (PUBLIC_APIS.getOrDefault(requestURI, false)) {
            // 공개 API는 IP 기반으로 제한
            String clientIp = getClientIp(request);
            key = "ip:" + clientIp + ":" + requestURI;
        } else {
            // 인증된 API는 username 기반으로 제한
            String username = getCurrentUsername();
            if (username == null) {
                // 인증이 필요한 API에 인증 정보가 없는 경우
                log.warn("Unauthenticated access to authenticated API: {}", requestURI);
                handleRateLimitExceeded(ExceptionCode.NOT_AUTHORIZED_USER, "username을 찾을 수 없습니다.");
                return false;
            }
            key = "user:" + username + ":" + requestURI;
        }

        // 현재 시간
        LocalDateTime now = LocalDateTime.now();

        // 요청 카운터 가져오기 또는 새로 생성
        RequestCounter counter = REQUEST_COUNT_MAP.computeIfAbsent(key, k -> new RequestCounter());

        // 이전 윈도우가 만료된 경우 카운터 초기화
        if (counter.getLastRequestTime().plusSeconds(DEFAULT_WINDOW_SIZE_SECONDS).isBefore(now)) {
            counter.resetCount();
        }

        // 요청 카운트 증가 및 시간 업데이트
        counter.incrementCount();
        counter.setLastRequestTime(now);

        // 요청 한도 초과 확인
        if (counter.getCount() > requestLimit) {
            log.warn("Rate limit exceeded for key: {}, URI: {} - Count: {}", key, requestURI, counter.getCount());

            // Rate Limit 초과 시 DB에 로그 저장
            saveRateLimitLog(request, requestURI, key);

            handleRateLimitExceeded(ExceptionCode.RATE_LIMIT, key);
            return false;
        }

        log.debug("Request count for key: {}, URI: {} - Count: {}/{}", key, requestURI, counter.getCount(), requestLimit);
        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");

        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }

        // 첫 번째 IP만 사용 (X-Forwarded-For는 여러 IP를 포함할 수 있음)
        int commaIndex = clientIp.indexOf(',');
        if (commaIndex != -1) {
            clientIp = clientIp.substring(0, commaIndex);
        }

        return clientIp;
    }

    private String getCurrentUsername() {
        UsersEntity usersEntity = userUtil.getUsersEntity();
        return usersEntity.getUsername();
    }

    private void handleRateLimitExceeded(ExceptionCode code, String message) {
        throw new AppException(code, message);
    }

    /**
     * Rate Limit 초과 시 DB에 로그를 저장하는 메소드
     */
    private void saveRateLimitLog(HttpServletRequest request, String requestURI, String key) {
        try {
            String clientIp = getClientIp(request);
            String username = null;

            // 키에서 사용자 이름 추출 (user: 로 시작하는 경우)
            if (key.startsWith("user:")) {
                String[] parts = key.split(":");
                if (parts.length > 1) {
                    username = parts[1];
                }
            } else {
                // 인증된 사용자인 경우 username 가져오기
                username = getCurrentUsername();
            }

            LoggingEntity logging = new LoggingEntity();
            logging.setIp(clientIp);
            logging.setCreateDt(LocalDateTime.now());
            logging.setUsername(username != null ? username : "anonymous");
            logging.setType("RATE_LIMIT");
            logging.setMessage("Rate limit exceeded for URI: " + requestURI);

            loggingRepository.save(logging);

            log.debug("Rate limit log saved to database for IP: {}, Username: {}, URI: {}",
                    clientIp, username, requestURI);
        } catch (Exception e) {
            // 로깅 실패 시 애플리케이션 동작에 영향을 주지 않도록 예외 처리
            log.error("Failed to save rate limit log to database", e);
        }
    }

    // 요청 카운터 클래스
    @Getter
    private static class RequestCounter {
        private int count;
        private LocalDateTime lastRequestTime;

        public RequestCounter() {
            this.count = 0;
            this.lastRequestTime = LocalDateTime.now();
        }

        public void incrementCount() {
            this.count++;
        }

        public void resetCount() {
            this.count = 1;  // 현재 요청을 포함하여 1로 초기화
        }

        public void setLastRequestTime(LocalDateTime lastRequestTime) {
            this.lastRequestTime = lastRequestTime;
        }
    }
}
