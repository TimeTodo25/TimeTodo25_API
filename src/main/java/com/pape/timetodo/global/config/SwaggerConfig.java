package com.pape.timetodo.global.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(){
        SecurityScheme apiKey = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("Bearer Token");

        // 프로토콜에 따른 서버 URL 설정
        Server server = new Server();
        server.setUrl("/api");
        // HTTPS 서버도 명시적으로 추가
        Server httpsServer = new Server();
        httpsServer.setUrl("https://api.timetodo.store/api");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("Bearer Token", apiKey))
                .info(new Info().title("Time Todo Swagger Docs").description("API 문서"))
                .servers(List.of(server, httpsServer))  // 두 서버 모두 추가
                .addSecurityItem(securityRequirement);
    }
}
