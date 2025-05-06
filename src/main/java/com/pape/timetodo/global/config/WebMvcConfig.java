package com.pape.timetodo.global.config;

import com.pape.timetodo.global.interceptor.RateLimitInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Rate Limit 인터셉터 등록
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/v1/**")  // 모든 API에 적용
                .excludePathPatterns(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/h2-console/**"
                ); // 제외 - 개발용
    }
}