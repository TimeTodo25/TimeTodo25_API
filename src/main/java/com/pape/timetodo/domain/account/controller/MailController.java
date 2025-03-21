package com.pape.timetodo.domain.account.controller;

import com.pape.timetodo.domain.account.model.mail.CertificationMailRQ;
import com.pape.timetodo.domain.account.model.mail.SendMailRQ;
import com.pape.timetodo.domain.account.model.user.IdFindingRS;
import com.pape.timetodo.domain.account.service.AccountMailService;
import com.pape.timetodo.global.exception.AppException;
import com.pape.timetodo.global.exception.ExceptionCode;
import com.pape.timetodo.global.jpa.entity.MailEntity.MailType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/mail")
@Tag(name = "메일 컨트롤러", description = "메일 발송 및 인증관련 API")
public class MailController {

    private final AccountMailService mailService;

    /**
     * 메일 발송, 비동기
     * @param rq
     * @return
     * @throws Exception
     */
    @PostMapping("/send/register")
    @Operation(summary = "회원가입 인증메일 발송", description = "유저의 계정을 추가할 때 사용하는 메일발송 입니다.")
    public DeferredResult<Boolean> sendCertMail(@Valid @RequestBody SendMailRQ rq) throws Exception{

        if(mailService.isAlreadyExistUsersMail(rq)) throw new AppException(ExceptionCode.EMAIL_DUPLICATE);

        DeferredResult<Boolean> deferredResult = new DeferredResult<>();

        new Thread(() -> {
            try {
                Boolean result = mailService.sendCertMail(rq, MailType.REGISTER_CERT);
                deferredResult.setResult(result);
            } catch (Exception e) {
                log.error("", e);
                throw new AppException(ExceptionCode.INTERNAL_SERVER_ERROR);
            }
        }).start();

        return deferredResult;
    }

    /**
     * 메일인증
     * @param rq
     * @return
     */
    @PutMapping("/certification/register")
    @Operation(summary = "회원가입 메일 인증", description = "유저의 계정을 추가할 때 사용하는 메일인증 입니다.")
    public ResponseEntity<Boolean> certificationRegisterMail(@Valid @RequestBody CertificationMailRQ rq){

        Boolean result = mailService.certificationMail(rq, MailType.REGISTER_CERT);

        return ResponseEntity.ok().body(result);
    }

    /**
     * 메일 발송, 비동기
     * @param rq SendMailRQ
     * @return DeferredResult
     * @throws Exception INTERNAL_SERVER_ERROR
     */
    @PostMapping("/send/finding")
    @Operation(summary = "아이디/비밀번호 찾기 인증메일 발송", description = "아이디/비밀번호를 찾을 때 사용하는 인증용 메일발송 입니다.")
    public DeferredResult<String> sendFindingCertMail(@Valid @RequestBody SendMailRQ rq) throws Exception {

        DeferredResult<String> deferredResult = new DeferredResult<>();

        if(!mailService.isAlreadyExistUsersMail(rq)) {
            deferredResult.setResult("회원 정보가 존재하지 않습니다.");
            return deferredResult;
        }

        new Thread(() -> {
            try {
                Boolean result = mailService.sendCertMail(rq, MailType.UPDATE_CERT);
                deferredResult.setResult(result.toString());
            } catch (Exception e) {
                log.error("", e);
                throw new AppException(ExceptionCode.INTERNAL_SERVER_ERROR);
            }
        }).start();

        return deferredResult;
    }

    /**
     * 아이디 찾기 메일 인증
     * @param rq IdFindingRQ
     * @return Boolean
     */
    @PutMapping("/certification/id")
    @Operation(summary = "아이디/비밀번호 찾기 메일 인증", description = "아이디/비밀번호를 찾을 때 사용하는 메일 인증 입니다.")
    public ResponseEntity<IdFindingRS> findId(@Valid @RequestBody CertificationMailRQ rq) {

        IdFindingRS result = mailService.findId(rq);

        return ResponseEntity.ok().body(result);
    }

}
