package com.wsn.powerstrip.communication.service.impl;

import com.wsn.powerstrip.communication.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


@Service
@Transactional
@Slf4j
public class MailServiceImpl implements MailService {

    private final String sender = "782864998@qq.com";

    final private JavaMailSender javaMailSender;

    public MailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }


    @Override
    public void sendMail(String emailAddress, String title, String content) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(sender);         //发送方
        helper.setTo(emailAddress);            //接收方
        helper.setSubject(title);       //发送主题
        helper.setText(content, true);
        javaMailSender.send(message);
        log.info("邮件发送成功");
    }
}
