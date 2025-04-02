package com.kevinolarte.security.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;


    public void sendVerificationEmail(String to, String subject, String text)
            throws MessagingException {
        System.out.println("3123123123123123");
        MimeMessage message = mailSender.createMimeMessage();
        System.out.println("3123123123123123");

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);
        System.out.println("Sending verification email");
        mailSender.send(message);
    }
}
