package com.pape.timetodo.domain.account.controller;

import com.pape.timetodo.domain.account.model.user.*;
import com.pape.timetodo.domain.account.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 사용자 아이디 중복 확인
     * @param rq NickCheckRQ
     * @return Boolean
     */
    @PostMapping("/nickname/check")
    @Operation(summary = "아이디 중복 체크 [중복 = True, 중복아님 = False]", description = "아이디 중복 확인기능 입니다.")
    public ResponseEntity<Boolean> isDuplicated(@Valid @RequestBody UsernameCheckRQ rq) {

        Boolean result = accountUserService.isDuplicated(rq);

        return ResponseEntity.ok().body(result);
    }

    /**
     * 설정 수정
     * @param rq UpdatePreferenceRQ
     * @return UpdatePreferenceRS
     */
    @PutMapping("/preference/update")
    @Operation(summary = "설정 수정", description = "설정값을 수정합니다 - 해당 설정에 변경사항 있을 시 true & on 되어야 하는 모든 설정 나열 / 없으면 false & 빈배열")
    public ResponseEntity<UpdatePreferenceRS> updateTodo(@Valid @RequestBody UpdatePreferenceRQ rq){

        UpdatePreferenceRS result = accountUserService.updatePreference(rq);

        return ResponseEntity.ok().body(result);
    }

    /**
     * 비밀번호 수정
     * @param rq NewPasswordRQ
     * @return NewPasswordRS
     */
    @PostMapping("/password")
    @Operation(summary = "비밀번호 수정", description = "비밀번호 수정 기능입니다.")
    public ResponseEntity<NewPasswordRS> findId(@Valid @RequestBody NewPasswordRQ rq) {

        NewPasswordRS result = accountUserService.updatePassword(rq);

        return ResponseEntity.ok().body(result);
    }

}
