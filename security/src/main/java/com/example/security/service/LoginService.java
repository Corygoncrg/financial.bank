package com.example.security.service;

import com.example.security.dto.AuthenticationDTO;
import com.example.security.dto.TokenJWTDTO;
import com.example.security.kafka.consumer.KafkaConsumer;
import com.example.shared.dto.UserDto;
import com.example.shared.model.User;
import com.example.shared.model.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class LoginService {

    @Autowired
    private TokenService tokenService;


    @Autowired
    private KafkaConsumer kafkaConsumer;


    /**
     * Method to return a token when login is successful
     * @param dto AuthenticationDTO with login and password values
     * @return TokenJWTDTO
     */
    public TokenJWTDTO login(AuthenticationDTO dto) {
        var user = fetchUser(dto.user());
        if (UserStatus.valueOf(user.status()) == UserStatus.NOT_ACTIVE) {
            throw new DisabledException("User is not active");
        }
        List<SimpleGrantedAuthority> authorities = user.authorities()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return performAuthentication(dto, authorities);
    }

    private UserDto fetchUser(String username) {
        return kafkaConsumer.requestUserByName(username);
    }

    private TokenJWTDTO performAuthentication(AuthenticationDTO dto, List<SimpleGrantedAuthority> authorities) {
        var user = new User();
        user.setName(dto.user());
        user.setPassword(dto.password());

        var tokenJWT = tokenService.createToken(user);
        return new TokenJWTDTO(tokenJWT);
    }

}
