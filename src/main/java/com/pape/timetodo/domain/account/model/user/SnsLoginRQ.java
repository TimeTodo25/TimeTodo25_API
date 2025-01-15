package com.pape.timetodo.domain.account.model.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
public class SnsLoginRQ {

    @NotNull
    @NotEmpty
    @Schema(description = "플랫 회원 고유아이디", example = "6S-EmS7-uHj7vBliH7QmLOo1KEMhar4JLj-Ff-LVwsY", implementation = String.class)
    private String providerId;

    @Schema(description = "닉네임", example = "김진경", implementation = String.class)
    private String nickname;

    @Schema(description = "이메일", example = "timetodo@gmail.com", implementation = String.class)
    private String email;

    @NotNull
    @Schema(description = "플랫폼 타입 [NAVER, GOOGLE]", example = "NAVER", implementation = PlatformType.class)
    private PlatformType platformType;

    
    @Getter
    @RequiredArgsConstructor
    public enum PlatformType{

        NAVER("네이버"),
        ;

        private final String desc;
    }
}
