package com.pape.timetodo.domain.account.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pape.timetodo.domain.account.model.user.SnsLoginRQ;
import com.pape.timetodo.domain.account.model.user.SnsLoginRS;
import com.pape.timetodo.domain.account.model.user.UserRegisterRQ;
import com.pape.timetodo.domain.account.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/v1/user")
@RequiredArgsConstructor
@Tag(name = "유저[계정]", description = "유저[계정]관련 API")
public class UserController {

    private final UserService accountUserService;

    /**
     * 회원가입
     * @param rq
     * @return
     */
    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "유저의 계정을 추가합니다.")
    public ResponseEntity<Boolean> register(@Valid @RequestBody UserRegisterRQ rq){

        Boolean result = accountUserService.userRegister(rq);
        
        return ResponseEntity.ok().body(result);
    }

    /**
     * SNS 로그인
     * @param rq
     * @return
     */
    @PostMapping("/sns/login")
    @Operation(summary = "SNS회원 로그인", description = "SNS 회원 로그인 기능 입니다.")
    public ResponseEntity<SnsLoginRS> snsLogin(@Valid @RequestBody SnsLoginRQ rq){

        SnsLoginRS result = accountUserService.snsLogin(rq);

        return ResponseEntity.ok().body(result);
    }
}
