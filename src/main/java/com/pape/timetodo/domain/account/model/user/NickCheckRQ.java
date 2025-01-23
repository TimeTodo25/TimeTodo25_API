package com.pape.timetodo.domain.account.model.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NickCheckRQ {

    @NotNull
    @Schema(description = "닉네임", example = "타임투두", implementation = String.class)
    private String nickname;
}
