package com.example.security.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class TokenAuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationService.class);

    @Autowired
    private KafkaAuthenticationService kafkaAuthService;

    @Autowired
    private TokenService tokenService;

    public UsernamePasswordAuthenticationToken authenticateToken(String token) {
        try {
            var subject = tokenService.getSubject(token); // Existing method in TokenService
            logger.debug("Token subject: {}", subject);

            var user = kafkaAuthService.getUserAuthentication(subject);
            if (user != null) {
                logger.debug("User authenticated: {}", user.getName());
                return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            } else {
                logger.debug("User not found for subject: {}", subject);
            }
        } catch (JWTVerificationException e) {
            logger.error("Token verification failed: {}", e.getMessage());
        }
        return null; // Return null if the token is invalid or user not found
    }

}
