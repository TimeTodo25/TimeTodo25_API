package com.pape.timetodo.domain.account.model.mail;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CertificationMailRQ {

    @Email
    @Schema(description = "이메일", example = "timetodo@gmail.com", implementation = String.class)
    private String email;

    @NotNull
    @Schema(description = "인증번호", example = "101922", implementation = String.class)
    private String certNum;

}
