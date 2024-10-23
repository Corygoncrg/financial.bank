package com.example.users.controller;

import com.example.users.dto.authentication.AuthenticationDTO;
import com.example.users.dto.authentication.TokenJWTDTO;
import com.example.users.dto.user.UserDto;
import com.example.users.dto.user.UserRegisterDto;
import com.example.users.dto.user.UserUpdateDto;
import com.example.users.model.User;
import com.example.users.model.UserStatus;
import com.example.users.model.UserValidator;
import com.example.users.repository.UserRepository;
import com.example.users.repository.UserValidatorRepository;
import com.example.users.service.EmailService;
import com.example.users.service.LoginService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    UserRepository repository;

    @Autowired
    UserValidatorRepository validatorRepository;

    @Autowired
    EmailService emailService;

    @GetMapping
    public List<UserDto> hi() {
        var user = repository.findAllUsers();

        return user.stream().map(UserDto::new).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<String> register(@ModelAttribute @Valid UserRegisterDto dto) {
        var user = new User(dto);
        if (!repository.existsByName(dto.username()) && !repository.existsByEmail(dto.email())) {
            System.out.println("Registered user: " + user);
            UserValidator validator = new UserValidator(user);

            emailService.sendPasswordEmail(user, validator);
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            repository.save(user);
            validatorRepository.save(validator);
            System.out.println(validator.getUuid());
            //TODO:
            // I also need to make it so that when a user logs in, that the server can tell which user logged to avoid letting him
            // delete himself from the database
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body("User with this username or email already exists!");
    }

    @PutMapping
    public ResponseEntity<String> updateUser(@Valid @RequestBody UserUpdateDto dto) {
        if (dto.id() == 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The admin cannot be edited");
        }
        if (!repository.existsByEmail(dto.email())) {
            var user = repository.getReferenceById(dto.id());
            user.update(dto);
            repository.save(user);
            return ResponseEntity.ok().body("The user " + user.getId() + " Has been updated");
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Another user with this email already exists!");
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> DeactivateUser(@PathVariable Long id) {
        if (id == 1L) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The admin cannot be deactivated");
        }
        User user = repository.getReferenceById(id);
        if (user.getStatus() == UserStatus.NOT_ACTIVE) {
            return ResponseEntity.badRequest().body("This user is already deactivated!");
        }
        user.deactivate();
        repository.save(user);
        return ResponseEntity.ok().body("The user " + user.getId() + " has been deactivated");
    }

    @Autowired
    private LoginService loginService;

    @PostMapping("login")
    private ResponseEntity<TokenJWTDTO> login(@RequestBody @Valid AuthenticationDTO dto) {
        return loginService.login(dto);
        //TODO: Add a log-in, where the user needs to be logged-in in order to users
    }

    @GetMapping("verify/{uuid}")
    public ResponseEntity<String> verifyUser(@PathVariable String uuid) {
        try {
            var validator = validatorRepository.findByUuid(uuid)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not verified"));

            if (validator.getExpirationDate().compareTo(Instant.now()) < 0) {
                validatorRepository.delete(validator);
                return ResponseEntity.badRequest().body("Expired expiration date");
            }

            var user = validator.getIdUser();
            user.validate();
            repository.save(user);

            return ResponseEntity.ok().body("User validated");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid UUID format");
        }
    }
}
