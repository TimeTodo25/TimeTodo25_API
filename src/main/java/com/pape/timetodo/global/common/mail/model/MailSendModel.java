package com.pape.timetodo.global.common.mail.model;

import lombok.Data;

@Data
public class MailSendModel {

    private String to;

    private String subject;

    private String message;

    private String certNum;

}
