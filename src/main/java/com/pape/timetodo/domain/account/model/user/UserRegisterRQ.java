package com.pape.timetodo.domain.account.model.user;

import org.hibernate.validator.constraints.Length;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRegisterRQ {

    @NotBlank
    @Length(min = 6, max = 50)
    @Schema(description = "아이디", example = "TEST_USER_ID", implementation = String.class)
    private String id;

    @NotNull
    @Schema(description = "비밀번호", example = "TEST_USER_PASSWORD", implementation = String.class)
    private String password;

    @Email
    @Schema(description = "이메일", example = "timetodo@gmail.com", implementation = String.class)
    private String email;

    @NotNull
    @Schema(description = "닉네임", example = "타임투투", implementation = String.class)
    private String nickname;

    @NotNull
    @Schema(description = "선택약관 동의", example = "true", implementation = boolean.class)
    private boolean optionTermsAgreed;
}
