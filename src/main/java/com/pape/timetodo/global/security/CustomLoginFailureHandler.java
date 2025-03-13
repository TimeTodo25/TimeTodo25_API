package com.pape.timetodo.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pape.timetodo.global.base.BaseErrorModel;
import com.pape.timetodo.global.exception.ExceptionCode;
import com.pape.timetodo.global.jpa.entity.LoggingEntity;
import com.pape.timetodo.global.jpa.entity.UsersEntity;
import com.pape.timetodo.global.jpa.repository.LoggingQueryRepository;
import com.pape.timetodo.global.jpa.repository.UsersRepository;
import com.pape.timetodo.global.security.model.LoginRQ;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {

    private UsersRepository usersRepository;

    private LoggingQueryRepository loggingQueryRepository;

    public CustomLoginFailureHandler(UsersRepository usersRepository, LoggingQueryRepository loggingQueryRepository){
        this.usersRepository = usersRepository;
        this.loggingQueryRepository = loggingQueryRepository;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        LoginRQ loginRQ = (LoginRQ)request.getSession().getAttribute(AuthConstants.LOGIN_INFO_SESSION.getValue());

        log.warn("exception => {}", exception.getMessage());
        log.warn("username => {}", loginRQ.getId());

        FailType failType = FailType.fromMessage(exception.getMessage());

        ExceptionCode loginFail = ExceptionCode.LOGIN_FAIL;

        LoggingEntity logging = new LoggingEntity();
        logging.setIp(request.getRemoteAddr());


        switch (failType) {
            case USER_NOT_FOUNT:
                logging.setType(LoggingType.LOGIN_NOT_USER.getValue());
                logging.setMessage("유저를 찾을 수 없습니다.");
                break;
            case ACOUNT_DISABLE:
                logging.setType(LoggingType.LOGIN_DISABLED.getValue());
                logging.setMessage("계정이 비활성화 되었습니다.");
                break;
            case ACOUNT_EXPRIED:
                logging.setType(LoggingType.LOGIN_EXPIRED.getValue());
                logging.setMessage("계정이 만료되었습니다.");
                logging.setCredentials("[PROTECT PASSWORD]");
                break;
            case ACOUNT_LOCK:
                logging.setType(LoggingType.LOGIN_LOCKED.getValue());
                logging.setMessage("계정이 잠겼습니다.");
                logging.setCredentials("[PROTECT PASSWORD]");
                break;
            case PASS_NOT_MATCH:
                logging.setType(LoggingType.LOGIN_WRONG_PW.getValue());
                logging.setMessage("비밀번호를 틀렸습니다.");

                UsersEntity users = usersRepository.findById(loginRQ.getId()).get();

                int loginFailCount = users.getPassFailCount();

                if(loginFailCount >= 4){
                    users.setAccountNonLock(false);
                } else {
                    users.setAccountNonLock(true);
                }

                users.setPassFailCount(loginFailCount+1);
                usersRepository.save(users);
                break;
            case PERMISSION_MISMATCH:
                break;
            case USER_DELETE:
                break;
            default:
                break;
        }

        loggingQueryRepository.insert(logging);

        BaseErrorModel baseBody = new BaseErrorModel();
        baseBody.setCode(loginFail.code());
        baseBody.setDesc(loginFail.message());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = objectMapper.writeValueAsString(baseBody);

        response.setStatus(loginFail.status()); // 상태 코드 401을 설정
        response.getWriter().write(responseBody);
    }
    
}
