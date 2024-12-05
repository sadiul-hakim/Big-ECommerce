package org.shopme.site.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shopme.common.pojo.MailStructure;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    @Value("${spring.mail.username:''}")
    public String fromMail;

    private final JavaMailSender mailSender;

    public void send(String toMail, MailStructure mail) {

        if (toMail.isEmpty() || mail.subject().isEmpty() || mail.mail().isEmpty()) {
            log.error("Invalid mail");
            return;
        }

        var simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(toMail);
        simpleMailMessage.setFrom(fromMail);
        simpleMailMessage.setSubject(mail.subject());
        simpleMailMessage.setText(mail.mail());

        mailSender.send(simpleMailMessage);
    }
}
