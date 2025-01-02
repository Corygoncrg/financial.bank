package com.example.security.service;

import com.example.security.dto.AuthenticationDTO;
import com.example.security.kafka.consumer.KafkaSecurityConsumer;
import com.example.shared.dto.UserDto;
import com.example.shared.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private KafkaSecurityConsumer kafkaSecurityConsumer;

    @Mock
    private TokenAuthenticationService authenticationService;

    @InjectMocks
    LoginService loginService;

    @Test
    @DisplayName("Test if user is NOT_ACTIVE")
    void testUserNotActive() {
        var dto = new AuthenticationDTO("user", "password");
        var user = new UserDto(1L, "user", "example@email.com", "password", "NOT_ACTIVE", List.of("1", "2"));
        when(kafkaSecurityConsumer.requestUserByName(dto.user())).thenReturn(user);



        assertThrows(DisabledException.class, () -> loginService.login(dto));
    }
    @Test
    @DisplayName("Test if user is ACTIVE")
    void testUserActive() {
        var dto = new AuthenticationDTO("user", "password");
        when(kafkaSecurityConsumer.requestUserByName(dto.user())).thenReturn(getUserDto());

        assertDoesNotThrow(() -> loginService.login(dto));
    }

    @Test
    @DisplayName("Test token successfully validated")
    void testValidateToken() {
        String token = "token";
        var user = new User();
        when(authenticationService.authenticateToken(token)).thenReturn(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));

        var result = loginService.validateToken(token);

        assertDoesNotThrow(() -> loginService.validateToken(token));
        assertTrue(result);

    }
    @Test
    @DisplayName("Test token is invalid")
    void testNotValidateToken() {
        String token = "token";
        when(authenticationService.authenticateToken(token)).thenReturn(null);

        var result = loginService.validateToken(token);

        assertFalse(result);

    }

    private UserDto getUserDto() {
        List<String> list = List.of("element1", "element2");
        return new UserDto(1L, "user", "example@email.com", "password", "ACTIVE", list);
    }
}