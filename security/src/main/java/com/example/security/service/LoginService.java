package com.example.security.service;

import com.example.security.dto.AuthenticationDTO;
import com.example.security.dto.TokenJWTDTO;
import com.example.security.kafka.consumer.KafkaConsumer;
import com.example.security.model.User;
import com.example.security.model.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
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
     * Method to return a token in the body when login is authenticated
     * @param dto AuthenticationDTO with login and password values
     * @return TokenJWTDTO
     */
    public TokenJWTDTO login(AuthenticationDTO dto) {
    var authenticationToken = new UsernamePasswordAuthenticationToken(dto.user(), dto.password());
    var user = kafkaConsumer.getUserAuthentication(authenticationToken.getName());
    if (UserStatus.valueOf(user.status()) == UserStatus.NOT_ACTIVE) {
        return null;
    }
    var authentication = manager.authenticate(authenticationToken);
    var tokenJWT = tokenService.createToken((User) authentication.getPrincipal());
        return new TokenJWTDTO(tokenJWT);
    }

}
