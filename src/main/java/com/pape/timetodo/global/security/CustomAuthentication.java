package com.pape.timetodo.global.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pape.timetodo.global.jpa.entity.UsersEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
public class CustomAuthentication implements AuthenticationProvider{
    
    
    private final UserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

        //입력한 ID, Password 조회
        String username = token.getName();
        String password = (String)token.getCredentials();

        //UserDetailsService를 통해 DB에서 조회한 사용자
        UsersEntity user = (UsersEntity)userDetailsService.loadUserByUsername(username);

        // 비밀번호 매칭되는지 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("PassWord Not Match");
        }

        return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
    
}
