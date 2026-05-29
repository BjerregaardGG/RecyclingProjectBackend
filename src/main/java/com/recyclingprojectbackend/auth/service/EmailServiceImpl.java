package com.recyclingprojectbackend.auth.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendPasswordResetEmail(String email, String resetToken) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(email);
        mailMessage.setSubject("Snatch - nulstil dit password");
        mailMessage.setText("Klik på linket for at nulstille dit password:\n\n"
                + "recyclingprojectfrontend://auth/reset-password?token=" + resetToken
                + "\n\nLinket udløber om 15 minutter.");
        javaMailSender.send(mailMessage);
    }
}
