package com.pape.timetodo.global.common.mail.service;

import com.pape.timetodo.global.common.mail.model.CertMailTemplateModel;
import com.pape.timetodo.global.common.mail.model.MailSendModel;
import com.pape.timetodo.global.common.template.service.TemplateService;
import com.pape.timetodo.global.jpa.entity.MailEntity;
import com.pape.timetodo.global.jpa.repository.MailRepository;
import com.pape.timetodo.global.util.RandomUtil;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

    private final TemplateService templateService;
    
    private final JavaMailSender javaMailSender;

    private final MailRepository mailRepository;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    public MailSendModel createCertiMail(String email){
        // 메일발송
        StringBuilder certNumBuilder = new StringBuilder();

        for(Integer num : RandomUtil.randomAllowDuplicationIntegerArray(9, 6)){
            certNumBuilder.append(num);
        }

        CertMailTemplateModel certMailTemplateParam = new CertMailTemplateModel();
        certMailTemplateParam.setCertNum(certNumBuilder.toString());

        String title = "타임투두 인증메일 입니다.";
        String message = templateService.getHtmlToString(certMailTemplateParam);

        MailSendModel mailSendModel = new MailSendModel();
        mailSendModel.setTo(email);
        mailSendModel.setSubject(title);
        mailSendModel.setMessage(message);
        mailSendModel.setCertNum(certNumBuilder.toString());

        // if(!sendMail(mailSendModel)) throw new AppException(ExceptionCode.EXTERNAL_MAIL_SEND);

        return mailSendModel;
    }
    
    /**
     * 메일발송
     * @param model
     * @param entity 메일 엔티티 (null 가능)
     * @return 메일 발송 성공 여부
     */
    public Boolean sendMail(MailSendModel model, MailEntity entity) throws Exception {
        
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        // try {
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
        mimeMessageHelper.setTo(model.getTo()); // 메일 수신자
        mimeMessageHelper.setSubject(model.getSubject()); // 메일 제목
        mimeMessageHelper.setText(model.getMessage(), true); // 메일 본문 내용, HTML 여부

        mailSendThread(mimeMessage, entity);

        // } catch (MessagingException e) {
            // log.error("", e);
            // return false;
        // }

        return true;
    }

    /**
     * 메일 발송 쓰레드 처리
     * @param mimeMessage 메일 메시지
     * @param entity 메일 엔티티 (null 가능)
     */
    private void mailSendThread(MimeMessage mimeMessage, MailEntity entity){
        new Thread(() -> {
            javaMailSender.send(mimeMessage);
            if (entity != null) {
                mailRepository.save(entity);
            }
        }).start();
    }

}
