package com.pape.timetodo.global.common.mail.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pape.timetodo.global.common.mail.model.MailSendModel;
import com.pape.timetodo.global.common.mail.service.MailService;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/v1/common/test")
@RequiredArgsConstructor
@Hidden
public class MailTestController {

    private final MailService mailService;

    @GetMapping("/mail")
    public ResponseEntity<?> mailTest() throws Exception{

        MailSendModel model = new MailSendModel();
        model.setTo("lkd9125@naver.com");
        model.setSubject("테스트 메일입니다 ㅋ");
        model.setMessage("메세지지지직~");

        mailService.sendMail(model, null);

        return ResponseEntity.ok().body(null);
    }
}
