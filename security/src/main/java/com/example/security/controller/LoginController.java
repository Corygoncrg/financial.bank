package com.example.security.controller;

import com.example.security.dto.AuthenticationDTO;
import com.example.security.dto.TokenJWTDTO;
import com.example.security.service.LoginService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping()
    public ResponseEntity<?> login(@RequestBody @Valid AuthenticationDTO dto) {
        var token = loginService.login(dto);
        if (token != null) {
            System.out.println(token);
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.badRequest().body("User is not longer active!");
    }

}
