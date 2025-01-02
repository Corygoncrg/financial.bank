package com.example.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.shared.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private User user;

    @Test
    @DisplayName("Test token is created")
    void testTokenIsCreated() throws IllegalAccessException, NoSuchFieldException {
        setSecret();

        when(user.getUsername()).thenReturn("testUser");

        String token = tokenService.createToken(user);

        assertNotNull(token);
        DecodedJWT decodedJWT = JWT.decode(token);
        assertEquals("com.example", decodedJWT.getIssuer());
        assertEquals("testUser", decodedJWT.getSubject());
        assertNotNull(decodedJWT.getExpiresAt());
    }

    @Test
    @DisplayName("Test JWTCreationException handling")
    void testJWTCreationExceptionHandling() throws NoSuchFieldException, IllegalAccessException {
        String testSecret = "testSecret";
        Field secretField = TokenService.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(tokenService, testSecret);

        try (var mockedAlgorithm = mockStatic(Algorithm.class)) {
            mockedAlgorithm.when(() -> Algorithm.HMAC256(testSecret))
                    .thenThrow(new JWTCreationException("Test Exception", null));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> tokenService.createToken(user));
            assertEquals("error trying to create token jwt", exception.getMessage());

        }
    }


    @Test
    @DisplayName("Test subject is returned")
    void testSubjectIsReturned() throws NoSuchFieldException, IllegalAccessException {
        setSecret();
        String testUser = "testUser";

        when(user.getUsername()).thenReturn(testUser);

        String token = tokenService.createToken(user);

        var subject = tokenService.getSubject(token);

        assertEquals(testUser, subject);
    }

    @Test
    @DisplayName("Test JWTVerificationException handling")
    void testJwtVerificationExceptionHandling() throws NoSuchFieldException, IllegalAccessException {
        setSecret();

        String token = "token";

        JWTVerificationException exception = assertThrows(JWTVerificationException.class, () -> tokenService.getSubject(token));
        assertEquals("Token JWT invalid or expired!", exception.getMessage());
    }

    @Test
    @DisplayName("Test JWTVerificationException for expired token")
    void testJwtVerificationExceptionForExpiredToken() throws NoSuchFieldException, IllegalAccessException {
        setSecret();
        var algorithm = Algorithm.HMAC256("testSecret");
        String token = JWT.create()
                .withIssuer("com.example")
                .withSubject(user.getUsername())
                .withExpiresAt(Date.from(Instant.now().minusSeconds(60)))
                .sign(algorithm);

        JWTVerificationException exception = assertThrows(JWTVerificationException.class, () -> tokenService.getSubject(token));
        assertEquals("Token JWT invalid or expired!", exception.getMessage());
    }

    private void setSecret() throws NoSuchFieldException, IllegalAccessException {
        String testSecret = "testSecret";
        Field secretField = TokenService.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(tokenService, testSecret);
    }
}