package com.pape.timetodo.domain.account.service;

import com.pape.timetodo.domain.account.model.user.*;
import com.pape.timetodo.global.constant.NotificationType;
import com.pape.timetodo.global.constant.SortType;
import com.pape.timetodo.global.exception.AppException;
import com.pape.timetodo.global.exception.ExceptionCode;
import com.pape.timetodo.global.jpa.entity.*;
import com.pape.timetodo.global.jpa.entity.AuthoritiesEntity.AuthorityId;
import com.pape.timetodo.global.jpa.entity.MailEntity.MailType;
import com.pape.timetodo.global.jpa.repository.LoggingRepository;
import com.pape.timetodo.global.jpa.repository.MailQueryRepository;
import com.pape.timetodo.global.jpa.repository.UserPreferencesRepository;
import com.pape.timetodo.global.jpa.repository.UsersRepository;
import com.pape.timetodo.global.security.JwtTokenProvider;
import com.pape.timetodo.global.security.model.TokenModel;
import com.pape.timetodo.global.security.model.UserType;
import com.pape.timetodo.global.util.UserUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository usersRepository;

    private final UserPreferencesRepository preferencesRepository;

    private final MailQueryRepository mailQueryRepository;

    private final LoggingRepository loggingRepository;

    private final UserUtil userUtil;

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Boolean userRegister(@Valid UserRegisterRQ rq) {

        // 아이디가 중복인지 확인
        if(usersRepository.findById(rq.getId()).isPresent()){
            throw new AppException(ExceptionCode.DATA_DUPLICATE, "ID 중복");
        }

        // 이메일이 중복인지 확인
        if(usersRepository.findByEmail(rq.getEmail()).isPresent()){
            throw new AppException(ExceptionCode.DATA_DUPLICATE, "EMAIL 중복");
        }

        Optional<MailEntity> mailEntityWrapper = mailQueryRepository.findByTop1EmailAndMailType(rq.getEmail(), MailType.REGISTER_CERT);

        // 메일인증이 되어있는지 확인
        if(!mailEntityWrapper.isPresent() || !mailEntityWrapper.get().getCertYn()) {
            throw new AppException(ExceptionCode.MAIL_NOT_AUTHENTICATION);
        }

        UsersEntity usersEntity = UsersEntity.builder()
            .username(rq.getId())
            .password(passwordEncoder.encode(rq.getPassword()))
            .email(rq.getEmail())
            .nickname(rq.getNickname())
            .enabled(true)
            .accountNonExpired(true)
            .accountNonLock(true)
            .passFailCount(0)
            .build();

        AuthorityId authorityId = AuthorityId.builder()
            .username(rq.getId())
            .authority(UserType.USER.getValue())
            .build();

        AuthoritiesEntity authoritiesEntity = AuthoritiesEntity.builder()
            .id(authorityId)
            .build();

        UserPreferencesEntity preferencesEntity = UserPreferencesEntity.builder()
                .categorySortTypes(EnumSet.noneOf(SortType.class))  // 정렬 기본값 설정 - none
                .notificationTypes(EnumSet.noneOf(NotificationType.class))  // 알림 기본값 설정 - none
                .optionTermsAgreed(rq.isOptionTermsAgreed()) // 선택 약관 동의 여부
                .createDt(LocalDateTime.now())
                .updateDt(LocalDateTime.now())
                .build();

        usersEntity.setAuthorities(authoritiesEntity);
        usersEntity.setUserPreferences(preferencesEntity);

        preferencesRepository.save(preferencesEntity);
        usersRepository.save(usersEntity);

        return true;
    }

    @Transactional
    public SnsLoginRS snsLogin(SnsLoginRQ rq) {

        String platformUsername = rq.getPlatformType().name() + "_" + rq.getProviderId();

        Optional<UsersEntity> userWrapper = usersRepository.findById(platformUsername);

        TokenModel tokenModel = null;

        if(userWrapper.isPresent()){ // 기존 회원이였을 시 바로 토큰발급
            UsersEntity existingUser = userWrapper.get(); 

            Authentication authentication = new UsernamePasswordAuthenticationToken(existingUser.getUsername(), existingUser.getPassword(), existingUser.getAuthorities());
            tokenModel = jwtTokenProvider.createToken(authentication);
        } else { // 회원이 아니였을 시 회원가입후 바로 토큰발급

            if(usersRepository.findByEmail(rq.getEmail()).isPresent()){
                throw new AppException(ExceptionCode.DATA_DUPLICATE, "EMAIL 중복");
            }

            UsersEntity newUser = UsersEntity.builder()
                .username(platformUsername)
                .password(passwordEncoder.encode(System.currentTimeMillis() + rq.getPlatformType().name()))
                .email(rq.getEmail())
                .nickname(rq.getNickname())
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLock(true)
                .passFailCount(0)
                .build();

            AuthorityId authorityId = AuthorityId.builder()
                .username(platformUsername)
                .authority(UserType.USER.getValue())
                .build();

            AuthoritiesEntity authoritiesEntity = AuthoritiesEntity.builder()
                .id(authorityId)
                .build();

            UserPreferencesEntity preferencesEntity = UserPreferencesEntity.builder()
                    .categorySortTypes(EnumSet.noneOf(SortType.class))  // 정렬 기본값 설정 - none
                    .notificationTypes(EnumSet.noneOf(NotificationType.class))  // 알림 기본값 설정 - none
                    .optionTermsAgreed(rq.isOptionTermsAgreed()) // 선택 약관 동의 여부
                    .createDt(LocalDateTime.now())
                    .updateDt(LocalDateTime.now())
                    .build();

            newUser.setAuthorities(authoritiesEntity);
            newUser.setUserPreferences(preferencesEntity);

            preferencesRepository.save(preferencesEntity);
            usersRepository.save(newUser);

            Authentication authentication = new UsernamePasswordAuthenticationToken(newUser.getUsername(), newUser.getPassword(), newUser.getAuthorities());
            tokenModel = jwtTokenProvider.createToken(authentication);
        }
        
        SnsLoginRS result = new SnsLoginRS();
        result.setTokenModel(tokenModel);

        return result;
    }

    @Transactional
    public LoginRS userLogin(@Valid UserLoginRQ rq) {
        LoginRS result = new LoginRS();
        result.setTokenModel(null);

        String id = rq.getId();
        String pw = rq.getPassword();
        Optional<UsersEntity> userWrapper = usersRepository.findById(id);

        if(userWrapper.isEmpty()) {
            result.setMessage("아이디 틀림");
            return result;
        }

        if(userWrapper.get().getPassFailCount() == 5) {
            result.setMessage("비밀번호 5회 이상 틀림: 잠긴 회원이므로 비밀번호 변경 요청");
            return result;
        }

        UsersEntity users = userWrapper.get();
        LoggingEntity logging = new LoggingEntity();
        logging.setIp(rq.getIp());
        logging.setCreateDt(LocalDateTime.now());
        logging.setUsername(users.getUsername());

        if(!users.getPassword().equals(passwordEncoder.encode(pw))) {

            int cnt = users.getPassFailCount()+1;
            users.setPassFailCount(cnt);
            usersRepository.save(users);

            logging.setType("FAIL");
            logging.setMessage("비밀번호 틀림");
            loggingRepository.save(logging);

            result.setMessage("비밀번호 "+cnt+"회 틀림");
            return result;
        }

        users.setPassFailCount(0);
        usersRepository.save(users);

        logging.setType("SUCCESS");
        logging.setMessage("로그인 성공");
        loggingRepository.save(logging);

        Authentication authentication = new UsernamePasswordAuthenticationToken(users.getUsername(), users.getPassword(), users.getAuthorities());
        TokenModel tokenModel = jwtTokenProvider.createToken(authentication);

        result.setMessage("로그인 성공: 환영합니다");
        result.setTokenModel(tokenModel);
        return result;
    }

    public boolean isDuplicated(UsernameCheckRQ rq) {
        return usersRepository.findById(rq.getUsername()).isPresent();
    }

    @Transactional
    public UpdatePreferenceRS updatePreference(@Valid UpdatePreferenceRQ rq) {

        UsersEntity usersEntity = userUtil.getUsersEntity();

        UserPreferencesEntity preferences = usersEntity.getUserPreferences();

        if(rq.getDdaySortType().isUpdate()) {
            preferences.setDdaySortType(rq.getDdaySortType().getValues());
        }
        if(rq.getCategorySortTypes().isUpdate()) {
            preferences.setCategorySortTypes(rq.getCategorySortTypes().getValues());
        }
        if(rq.getNotificationTypes().isUpdate()) {
            preferences.setNotificationTypes(rq.getNotificationTypes().getValues());
        }

        LocalDateTime now = LocalDateTime.now();
        preferences.setUpdateDt(LocalDateTime.now());
        preferencesRepository.save(preferences);

        UpdatePreferenceRS result = new UpdatePreferenceRS();
        result.setUpdateDt(now);

        return result;
    }

    @Transactional
    public NewPasswordRS updatePassword(@Valid NewPasswordRQ rq) {

        NewPasswordRS result = new NewPasswordRS();

        Optional<UsersEntity> user = usersRepository.findById(rq.getId());
        if(user.isEmpty()) {
            result.setAnswer("회원 정보가 존재하지 않습니다.");
            return result;
        }

        UsersEntity newUser = user.get();
        newUser.setPassword(passwordEncoder.encode(rq.getPassword()));
        newUser.setPassFailCount(0);
        newUser.setUpdateDt(LocalDateTime.now());

        usersRepository.save(newUser);

        result.setAnswer("비밀 번호가 수정되었습니다.");

        return result;
    }

    /**
     * 탈퇴 시 회원 정보 논리 삭제
     */
    @Transactional
    public void withdraw() {
        UsersEntity user = userUtil.getUsersEntity();
        user.setEmail(user.getEmail()+"_deleted");
        user.setEnabled(false);
        user.setDeleteDt(LocalDateTime.now());
    }

    /**
     * 탈퇴 30일 후 회원 정보 물리 삭제
     * 매일 자정 직후 실행
     */
    @Scheduled(cron = "5 0 0 * * *") // 매일 자정 5초
    @Transactional
    public void deleteUser() {
        LocalDateTime withdrawDate = LocalDateTime.now().minusDays(30);
        List<UsersEntity> withdrawUsers = usersRepository.findAllByDeleteDtBefore(withdrawDate);
        if(!withdrawUsers.isEmpty())
            usersRepository.deleteAll(withdrawUsers);
    }
}
