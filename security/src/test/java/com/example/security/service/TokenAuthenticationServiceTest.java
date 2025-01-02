package com.example.security.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.shared.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenAuthenticationServiceTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private KafkaAuthenticationService kafkaAuthenticationService;

    @InjectMocks
    private TokenAuthenticationService authenticationService;

    @Test
    @DisplayName("Test subject not found")
    void testNotValidateToken() {
        String token = "token";
        JWTVerificationException exception = new JWTVerificationException("Invalid token");
        when(tokenService.getSubject(token)).thenThrow(exception);

        var result = authenticationService.authenticateToken(token);

        assertNull(result);
        verify(tokenService).getSubject(token);
    }

    @Test
    @DisplayName("Test authentication token is returned")
    void TestAuthenticationTokenIsReturned() {
        String token = "token";
        var user = new User();
        var authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        when(kafkaAuthenticationService.getUserAuthentication(any())).thenReturn(user);

        var result = authenticationService.authenticateToken(token);

        assertEquals(authToken, result);
    }
    @Test
    @DisplayName("Test authentication token is not found")
    void TestAuthenticationTokenIsNotReturned() {
        String token = "token";
        when(kafkaAuthenticationService.getUserAuthentication(any())).thenReturn(null);

        var result = authenticationService.authenticateToken(token);

        assertNull(result);
        verify(kafkaAuthenticationService).getUserAuthentication(any());
    }


}