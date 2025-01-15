package com.pape.timetodo.global.base;

import lombok.Data;

@Data
public class BaseModel {
    
    private int code;

    private Object body;

    public BaseModel() {

    }

    public BaseModel (Object body) {
        this.code = 200;
        this.body = body;
    }

    public BaseModel (int code, Object body) {
        this.code = code;
        this.body = body;
    }

}
