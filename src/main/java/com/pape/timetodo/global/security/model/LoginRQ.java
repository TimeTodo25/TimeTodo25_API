package com.pape.timetodo.global.security.model;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRQ {

    @Length(min = 0, max = 50)
    @NotNull
    private String username;

    private String password;
}
