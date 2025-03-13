package com.pape.timetodo.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pape.timetodo.global.exception.AppException;
import com.pape.timetodo.global.exception.ExceptionCode;
import com.pape.timetodo.global.jpa.entity.UsersEntity;
import com.pape.timetodo.global.jpa.repository.UsersRepository;
import com.pape.timetodo.global.security.model.LoginRQ;
import com.pape.timetodo.global.security.model.LoginRS;
import com.pape.timetodo.global.security.model.TokenModel;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Optional;

public class JwtAdminAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

    private AuthenticationManager authenticationManager;

    private CustomAdminAuthentication customAdminAuthentication;

    private JwtTokenProvider jwtTokenProvider;

    private UsersRepository usersRepository;

    public JwtAdminAuthenticationFilter(AuthenticationManager authenticationManager, CustomAdminAuthentication customAdminAuthentication, JwtTokenProvider jwtTokenProvider, UsersRepository usersRepository){
        super(authenticationManager);
        this.authenticationManager = authenticationManager;
        this.customAdminAuthentication = customAdminAuthentication;
        this.jwtTokenProvider = jwtTokenProvider;
        this.usersRepository = usersRepository;
        setFilterProcessesUrl("/v1/admin/login");
    }

    

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            LoginRQ users = objectMapper.readValue(request.getInputStream(), LoginRQ.class);

            request.getSession().setAttribute(AuthConstants.LOGIN_INFO_SESSION.getValue(), users);

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(users.getId(), users.getPassword());

            return customAdminAuthentication.authenticate(usernamePasswordAuthenticationToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
       return null;
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        TokenModel tokenModel = jwtTokenProvider.createToken(authResult);

        LoginRS result = new LoginRS();
        result.setToken(tokenModel);

        String jsonStr = new ObjectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(result);

        response.setContentType("application/json");
        response.getWriter().write(jsonStr);

        Optional<UsersEntity> usersWraper = usersRepository.findById(authResult.getName());

        if(!usersWraper.isPresent()) throw new AppException(ExceptionCode.DATA_NOT_FIND);

        UsersEntity users = usersWraper.get();

        users.setPassFailCount(0);
        users.setRefreshToken(tokenModel.getRefreshToken());

        usersRepository.save(users);

        this.getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
    }

}
