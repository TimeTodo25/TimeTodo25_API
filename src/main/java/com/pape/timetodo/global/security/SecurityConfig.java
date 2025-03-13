package com.pape.timetodo.global.security;

import com.pape.timetodo.global.jpa.repository.UsersRepository;
import com.pape.timetodo.global.security.model.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    private final UsersRepository usersRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationConfiguration authenticationConfiguration;

    private final CustomLoginSuccessHandler customLoginSuccessHandler;

    private final CustomLoginFailureHandler customLoginFailureHandler;

    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    private final CustomAuthenticationEntryPointHandler customAuthenticationEntryPointHandler;

    private final String[] PERMIT_URL = {
            // 개발용
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/h2-console/**",
            // user 로그인 전
            "/v1/user/register",
            "/v1/user/sns/login",
            "/v1/user/nickname/check",
            "/v1/mail/send/register",
            "/v1/mail/certification/register",
            "/v1/mail/send/finding",
            "/v1/mail/certification/id",
            "/v1/noti/receive/agree",
            "/v1/user/password", // 이게 여기 있는 게 맞냐... 난 모르겠다...
    };

     private final String[] AUTHENTICATION_URL = {
             // Todo_
             "/v1/todo/create",
             "/v1/todo/home",
             "/v1/todo/*/delete",
             "/v1/todo/update",
             "/v1/todo/regist/todo/timer",
             "/v1/todo/detail/*",
             "v1/todo/detail/*/timer",

             // Routine
             "/v1/routine/routine/register",
             "/v1/routine/my",
             "/v1/routine/detail/*",
             "/v1/routine/routine/update",
             "/v1/routine/routine/delete/*",

             // Category
             "/v1/category/create",
             "/v1/category/update",
             "/v1/category/my",
             "/v1/category/detail/*",
             "/v1/category/*/delete",
             "/v1/category/*/end",

             // Dday
             "/v1/dday/register/d-day",
             "/v1/dday/update",
             "/v1/dday/my",
             "/v1/dday/detail/*",
             "/v1/dday/*/delete",
             "/v1/dday/complete/*",

             // User - 회원가입, 로그인, 닉네임 중복검사 제외
             "/v1/user/preference/update",
             //"/v1/user/password",

     };


    private final String[] COMPANY_AUTH = {
            // "/company/**"
    };

    private final String[] ADMIN_AUTH = {
            "/admin/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(this.corsConfigurationSource()))
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .formLogin(formLogin -> formLogin.disable())
                .authorizeHttpRequests(authorizeRequests -> {
                    authorizeRequests
                            .requestMatchers(PERMIT_URL).permitAll()
                            .requestMatchers(AUTHENTICATION_URL).hasAnyAuthority(UserType.USER.getValue(), UserType.COMPANY.getValue(), UserType.ADMIN.getValue())
                            .requestMatchers(COMPANY_AUTH).hasAnyAuthority(UserType.COMPANY.getValue(), UserType.ADMIN.getValue())
                            .requestMatchers(ADMIN_AUTH).hasAnyAuthority(UserType.ADMIN.getValue())
                            .anyRequest().authenticated();
                })
                .addFilter(jwtAuthenticationFilter())
                .addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> {
                    exception.accessDeniedHandler(this.customAccessDeniedHandler);
                    exception.authenticationEntryPoint(this.customAuthenticationEntryPointHandler);
                })
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("X-Requested-With", "Content-Type", "Authorization", "X-XSRF-token"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public CustomAuthentication customAuthentication(){
        return new CustomAuthentication(userDetailsService, passwordEncoder());
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(){
        return new JwtAuthorizationFilter(jwtTokenProvider, usersRepository);
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManagerBean(), jwtTokenProvider, usersRepository);
        jwtAuthenticationFilter.setAuthenticationSuccessHandler(customLoginSuccessHandler);
        jwtAuthenticationFilter.setAuthenticationFailureHandler(customLoginFailureHandler);

        return jwtAuthenticationFilter;
    }

}