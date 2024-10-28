package com.example.users.controller;

import com.example.users.dto.authentication.AuthenticationDTO;
import com.example.users.dto.authentication.TokenJWTDTO;
import com.example.users.dto.user.UserDto;
import com.example.users.dto.user.UserRegisterDto;
import com.example.users.dto.user.UserUpdateDto;
import com.example.users.model.*;
import com.example.users.service.LoginService;
import com.example.users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private LoginService loginService;

    @GetMapping
    public List<UserDto> listUsers() {
        var user = service.listUsers();

        return user.stream().map(UserDto::new).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<String> register(@ModelAttribute @Valid UserRegisterDto dto) {
        boolean isRegistered = service.register(dto);

        if (isRegistered) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User with this username or email already exists!");
        }
    }


    @PutMapping
    public ResponseEntity<String> updateUser(@Valid @RequestBody UserUpdateDto dto) {
        UpdateUserResult result = service.updateUser(dto);
        return switch (result) {
            case ADMIN_EDIT_DENIED -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The admin cannot be edited");
            case EMAIL_CONFLICT -> ResponseEntity.status(HttpStatus.CONFLICT).body("Another user with this email already exists!");
            case SUCCESS -> ResponseEntity.ok().body("The user has been updated successfully");
        };
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> DeactivateUser(@PathVariable Long id) {
        DeactivateUserResult result = service.deactivateUser(id);
        return switch (result) {
            case ADMIN_DELETE_DENIED -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The admin cannot be deactivated");
            case USER_ALREADY_NOT_ACTIVE -> ResponseEntity.badRequest().body("This user is already deactivated!");
            case SUCCESS -> ResponseEntity.ok().body("The user " + id + " has been deactivated");
        };
    }

    @PostMapping("login")
    public ResponseEntity<TokenJWTDTO> login(@RequestBody @Valid AuthenticationDTO dto) {
        var token = loginService.login(dto);
        System.out.println(token);
        return ResponseEntity.ok(token);
        //TODO: Add a log-in, where the user needs to be logged-in in order to users
        // When the user logs-in, the token will be saved somewhere in his computer, and with that
        // he'll be able to access the pages, and from the token we are able to identify the user
        //
    }

    @GetMapping("verify/{uuid}")
    public ResponseEntity<String> verifyUser(@PathVariable String uuid) {
        VerifyUserResult result = service.verifyUser(uuid);

        return switch (result){
            case USER_NOT_VERIFIED -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not verified");
            case EXPIRED_VALIDATION_DATE -> ResponseEntity.badRequest().body("Expired expiration date");
            case SUCCESS -> ResponseEntity.ok().body("User validated");
            case INVALID_UUID_FORMAT -> ResponseEntity.badRequest().body("Invalid UUID format");
        };
    }
}
