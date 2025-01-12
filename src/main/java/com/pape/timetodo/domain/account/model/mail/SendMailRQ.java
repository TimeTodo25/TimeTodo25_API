package com.pape.timetodo.domain.account.model.mail;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class SendMailRQ {

    @Email
    @Schema(description = "이메일", example = "testSchedule@schedulewith.com", implementation = String.class)
    private String email;

}
