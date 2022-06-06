package com.wsn.powerstrip.communication.service;


import javax.mail.MessagingException;

public interface MailService {
    void sendMail(String emailAddress, String title, String content) throws MessagingException;
}
