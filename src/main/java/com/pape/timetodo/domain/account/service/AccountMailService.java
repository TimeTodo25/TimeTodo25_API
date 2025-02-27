package com.pape.timetodo.domain.account.service;

import com.pape.timetodo.domain.account.model.mail.CertificationMailRQ;
import com.pape.timetodo.domain.account.model.mail.SendMailRQ;
import com.pape.timetodo.domain.account.model.user.IdFindingRS;
import com.pape.timetodo.global.common.mail.model.MailSendModel;
import com.pape.timetodo.global.common.mail.service.MailService;
import com.pape.timetodo.global.exception.AppException;
import com.pape.timetodo.global.exception.ExceptionCode;
import com.pape.timetodo.global.jpa.entity.MailEntity;
import com.pape.timetodo.global.jpa.entity.MailEntity.MailType;
import com.pape.timetodo.global.jpa.entity.UsersEntity;
import com.pape.timetodo.global.jpa.repository.MailQueryRepository;
import com.pape.timetodo.global.jpa.repository.MailRepository;
import com.pape.timetodo.global.jpa.repository.UsersRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountMailService {
    
    private final MailService mailService;

    private final MailQueryRepository mailQueryRepository;

    private final UsersRepository usersRepository;

    private final MailRepository mailRepository;

    private short SMS_AUTH_TIME = 5; // 메일 인증시간 5분
    
    
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

    @Transactional
    public IdFindingRS findId(CertificationMailRQ rq) {

        Boolean isCerificated = this.certificationMail(rq, MailType.UPDATE_CERT);

        IdFindingRS idFindingRS = new IdFindingRS();
        if(!isCerificated) {
            idFindingRS.setUserId(null);
        } else {
            Optional<UsersEntity> usersEntity = usersRepository.findByEmail(rq.getEmail());
            if(usersEntity.isEmpty()) {
                idFindingRS.setUserId("회원 정보가 존재하지 않습니다.");
                return idFindingRS;
            }

            String username = usersEntity.get().getUsername();
            if(username.startsWith("NAVER_") && username.length() > 20) {
                idFindingRS.setUserId("Naver 소셜 로그인 회원입니다.");
            }
            else {
                idFindingRS.setUserId(username.substring(0, 3) + "*".repeat(username.length()-3));
            }
        }

        return idFindingRS;
    }

    public boolean isNotCertMail(SendMailRQ rq) {
        Optional<UsersEntity> usersEntity = usersRepository.findByEmail(rq.getEmail());
        return usersEntity.isEmpty();
    }
}
