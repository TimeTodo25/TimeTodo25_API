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
            "/**", // 차후 삭제
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/h2-console/**",
    };

    // private final String[] AUTHENTICATION_URL = {

    //     // Todo
    //     "/v1/todo/create",
    //     "/v1/todo/home",
    //     "/v1/todo/register/routine",
    //     "/v1/todo/*/delete",
    //     "/v1/todo/update",
    //     "/v1/todo/regist/todo/timer",

    //     // Category
    //     "/v1/category/create",
    //     "/v1/category/update",
    //     "/v1/category/my",
    // };

    private final String[] AUTHENTICATION_URL = {

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
                            .requestMatchers(AUTHENTICATION_URL).hasAnyAuthority(UserType.USER.getValue(), UserType.COMPANY.getValue(),UserType.ADMIN.getValue())
                            .requestMatchers(COMPANY_AUTH).hasAnyAuthority(UserType.COMPANY.getValue(),UserType.ADMIN.getValue())
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