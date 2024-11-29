package com.example.security.service;

import com.example.security.dto.AuthenticationDTO;
import com.example.security.dto.TokenJWTDTO;
import com.example.security.dto.UserDto;
import com.example.security.kafka.consumer.KafkaConsumer;
import com.example.security.model.User;
import com.example.security.model.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private KafkaConsumer kafkaConsumer;


    /**
     * Method to return a token when login is successful
     * @param dto AuthenticationDTO with login and password values
     * @return TokenJWTDTO
     */
    public TokenJWTDTO login(AuthenticationDTO dto) {
        // Step 1: Fetch user and validate status
        var user = fetchUser(dto.user());
        if (UserStatus.valueOf(user.status()) == UserStatus.NOT_ACTIVE) {
            throw new DisabledException("User is not active");
        }

        // Step 2: Perform authentication
        return performAuthentication(dto);
    }

    private UserDto fetchUser(String username) {
        return kafkaConsumer.requestUserByName(username);
    }

    private TokenJWTDTO performAuthentication(AuthenticationDTO dto) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dto.user(), dto.password());
        var authentication = manager.authenticate(authenticationToken);
        var tokenJWT = tokenService.createToken((User) authentication.getPrincipal());
        return new TokenJWTDTO(tokenJWT);
    }

}
