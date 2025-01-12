package com.pape.timetodo.domain.account.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/noti")
public class NotificationController {


    @PostMapping("/receive/agree")
    public ResponseEntity<?> fcmNotificationReceiveAgree(){

        

        return ResponseEntity.ok().build();
    }
}
