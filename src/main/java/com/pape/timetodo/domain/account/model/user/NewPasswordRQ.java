package com.pape.timetodo.domain.account.model.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class NewPasswordRQ {

    @NotBlank
    @Length(min = 6, max = 50)
    @Schema(description = "아이디", example = "TEST_USER_ID", implementation = String.class)
    private String id;

    @NotNull
    @Schema(description = "새 비밀번호", example = "NEW_USER_PASSWORD", implementation = String.class)
    private String password;

}
