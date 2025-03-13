package com.pape.timetodo.global.security.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginRQ {

    @Length(min = 0, max = 50)
    @NotNull
    private String id;

    private String password;
}
