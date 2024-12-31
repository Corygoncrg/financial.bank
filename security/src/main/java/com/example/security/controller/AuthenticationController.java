package com.example.security.controller;

import com.example.security.dto.AuthenticationDTO;
import com.example.security.service.LoginService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.shared.util.HeaderConstants.CORRELATION_ID;

@RestController
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private LoginService loginService;

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestHeader(CORRELATION_ID) String correlationId, @RequestBody @Valid AuthenticationDTO dto) {
        logger.debug("Correlation ID found for user log-in: {} ", correlationId);

        var token = loginService.login(dto);
        if (token != null) {
            System.out.println(token);
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.badRequest().body("User is not longer active!");
    }

    @PostMapping("validate")
    public ResponseEntity<?> validateToken(@RequestBody String token) {
        boolean isValid = loginService.validateToken(token);

        if (isValid) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
    }

}
