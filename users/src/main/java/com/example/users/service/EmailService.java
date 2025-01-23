package com.example.users.service;

import org.springframework.beans.factory.annotation.Value;
import com.example.shared.model.User;
import com.example.shared.model.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${host.name}")
    private String hostname;

    private final String VALIDATE_URL = hostname + "/users/verify/";

    public void sendPasswordEmail(User user, UserValidator validator) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(user.getEmail());
        msg.setSubject("Email verification");
        msg.setText("Dear " + user.getName() + ", your password has been set to: " + user.getPassword() +
                "\nYour link to validate your account is: " + VALIDATE_URL + validator.getUuid());
        try {
            mailSender.send(msg);
        }
        catch (MailException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void sendVerificationKey(User user, UserValidator validator) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(user.getEmail());
        msg.setSubject("New verification key");
        msg.setText("Dear " + user.getName() + ", Your new validation key is: " + validator.getUuid() +
                "\n email verification link: " + VALIDATE_URL + validator.getUuid());
        try {
            mailSender.send(msg);
        }
        catch (MailException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
