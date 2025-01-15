package com.pape.timetodo.global.base;

import lombok.Data;

@Data
public class BaseErrorModel {

    private String code;

    private String message;

    private String desc;

}
