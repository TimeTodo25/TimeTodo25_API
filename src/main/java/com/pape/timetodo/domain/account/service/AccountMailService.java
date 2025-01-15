package com.pape.timetodo.domain.account.service;

import com.pape.timetodo.domain.account.model.mail.CertificationMailRQ;
import com.pape.timetodo.domain.account.model.mail.SendMailRQ;
import com.pape.timetodo.global.common.mail.model.MailSendModel;
import com.pape.timetodo.global.common.mail.service.MailService;
import com.pape.timetodo.global.exception.AppException;
import com.pape.timetodo.global.exception.ExceptionCode;
import com.pape.timetodo.global.jpa.entity.MailEntity;
import com.pape.timetodo.global.jpa.entity.MailEntity.MailType;
import com.pape.timetodo.global.jpa.repository.MailQueryRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountMailService {
    
    private final MailService mailService;

    private final MailQueryRepository mailQueryRepository;

    private short SMS_AUTH_TIME = 5; // 문자 인증시간 5분
    
    
    public Boolean sendCertMail(SendMailRQ rq, MailType mailType) throws Exception {
        // 메일 Model Create
        MailSendModel mailSendModel = mailService.createCertiMail(rq.getEmail());
        

        // 메일 Entity
        MailEntity entity = MailEntity.builder()
            .mailType(mailType)
            .title(mailSendModel.getSubject())
            .content(mailSendModel.getMessage())
            .email(rq.getEmail())
            .certNum(mailSendModel.getCertNum())
            .certYn(false)
            .build();

        // 메일발송 및 메일 Entity저장
        mailService.sendMail(mailSendModel, entity);

        return true;
    }

    @Transactional
    public Boolean certificationMail(@Valid CertificationMailRQ rq, MailType mailType) {

        MailEntity entity = mailQueryRepository.findByTop1EmailAndMailType(rq.getEmail(), mailType)
            .orElseThrow(() -> new AppException(ExceptionCode.EMAIL_NOT_SEND));

        return this.certificationMail(entity, rq.getCertNum());
    }

    /**
     * 인증로직
     * @param entity
     * @param certNum
     * @return
     */
    private Boolean certificationMail(MailEntity entity, String certNum){

        LocalDateTime now = LocalDateTime.now().minusMinutes(SMS_AUTH_TIME);
        LocalDateTime certTime = entity.getCreateDt();

        Boolean timeCheck = now.isBefore(certTime);

        if(!timeCheck) throw new AppException(ExceptionCode.TIME_OUT);
        if(!entity.getCertNum().equals(certNum)) throw new AppException(ExceptionCode.NOT_AUTHENTICATION_USER);

        entity.setCertYn(true);
        entity.setUpdateDt(LocalDateTime.now());

        return true;
    }

}
