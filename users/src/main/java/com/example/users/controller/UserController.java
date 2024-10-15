package com.example.users.controller;

import com.example.users.dto.UserRegisterDto;
import com.example.users.model.User;
import com.example.users.repository.UserRepository;
import com.example.users.service.EmailService;
import jakarta.validation.Valid;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserRepository repository;

    @Autowired
    EmailService emailService;

    @GetMapping
    private String hi() {
        return repository.findAll().toString();
    }

    @PostMapping
    public ResponseEntity<?> register(@ModelAttribute @Valid UserRegisterDto dto) {
        var user = new User(dto);
        if (!repository.existsByName(dto.username()) && !repository.existsByEmail(dto.email())) {
            System.out.println("Registered user: " + user);
            emailService.sendPasswordEmail(user);
//TODO: Add Bcrypt and save the password as that, Send a link for the user to validate his account status
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body("User with this username or email already exists!");
    }
}
