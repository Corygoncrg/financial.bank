package com.example.users.controller;

import com.example.users.dto.authentication.AuthenticationDTO;
import com.example.users.dto.user.UserDto;
import com.example.users.dto.user.UserRegisterDto;
import com.example.users.dto.user.UserUpdateDto;
import com.example.users.model.DeactivateUserResult;
import com.example.users.model.UpdateUserResult;
import com.example.users.model.VerifyUserResult;
import com.example.users.service.LoginService;
import com.example.users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
        boolean isNotRegistered = service.register(dto);

        if (isNotRegistered) {
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
    public ResponseEntity<?> login(@RequestBody @Valid AuthenticationDTO dto) {
        var token = loginService.login(dto);
        if (token != null) {
            System.out.println(token);
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.badRequest().body("User is not longer active!");
    }

    @GetMapping("verify/{uuid}")
    public ResponseEntity<String> verifyUser(@PathVariable String uuid) {
        //TODO: When the user receives the email, there's a link that he can click to go on the site, and insert
        // the validation key to validate his account
        VerifyUserResult result = service.verifyUser(uuid);

        return switch (result){
            case USER_NOT_VERIFIED -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not verified");
            case EXPIRED_VALIDATION_DATE -> ResponseEntity.badRequest().body("Expired expiration date");
            case SUCCESS -> ResponseEntity.ok().body("User validated");
            case INVALID_UUID_FORMAT -> ResponseEntity.badRequest().body("Invalid UUID format");
        };
    }

    @GetMapping("current-user")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {

        var user = service.checkCurrentUser(userDetails);

        return ResponseEntity.ok(user);
    }
}
