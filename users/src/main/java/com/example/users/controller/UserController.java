package com.example.users.controller;

import com.example.users.dto.UserDto;
import com.example.users.dto.UserRegisterDto;
import com.example.users.dto.UserUpdateDto;
import com.example.users.model.User;
import com.example.users.model.UserAdapter;
import com.example.users.model.UserStatus;
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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserRepository repository;

    @Autowired
    EmailService emailService;

    @GetMapping
    public List<UserDto> hi() {
        var user = repository.findAllUsers();

        return user.stream().map(UserAdapter::userToDto).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<String> register(@ModelAttribute @Valid UserRegisterDto dto) {
        var user = new User(dto);
        if (!repository.existsByName(dto.username()) && !repository.existsByEmail(dto.email())) {
            System.out.println("Registered user: " + user);
            emailService.sendPasswordEmail(user);

        //TODO: Add Spring Security Bcrypt and save the password as that, Send a link for the user to validate his account status
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body("User with this username or email already exists!");
    }

    @PutMapping
    public ResponseEntity<String> updateUser(@Valid @RequestBody UserUpdateDto dto) {
        if (dto.id() == 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The admin cannot be edited");
        }
        var user = repository.getReferenceById(dto.id());
        user.update(dto);
        repository.save(user);
        return ResponseEntity.ok().body("The user " + user.getId() + " Has been updated");
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> DeactivateUser(@PathVariable Long id) {
        if (id == 1L) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The admin cannot be deactivated");
        }
        //TODO- Validate that the user currently logged in is not able to deactivate itself
        User user = repository.getReferenceById(id);
        if (user.getStatus() == UserStatus.NOT_ACTIVE) {
            return ResponseEntity.badRequest().body("This user is already deactivated!");
        }
        user.deactivate();
        repository.save(user);
        return ResponseEntity.ok().body("The user " + user.getId() + " has been deactivated");
    }
}
