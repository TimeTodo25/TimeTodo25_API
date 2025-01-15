package com.pape.timetodo.domain.account.model.user;

import com.pape.timetodo.global.security.model.TokenModel;

import lombok.Data;

@Data
public class SnsLoginRS {

    private TokenModel tokenModel;
}
