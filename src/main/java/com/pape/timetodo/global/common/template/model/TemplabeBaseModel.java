package com.pape.timetodo.global.common.template.model;

import java.util.Map;

import lombok.Data;

@Data
public abstract class TemplabeBaseModel {

    private String template;

    public abstract void init();

    public abstract Map<String, Object> getParam();
}
