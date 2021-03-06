package com.gmail.buer2012.service;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailSenderService {
    
    private JavaMailSender javaMailSender;
    
    public void sendEmail(String recipient, String content, String topic) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient);
        message.setSubject(topic);
        message.setText(content);
        
        javaMailSender.send(message);
    }
    
}
