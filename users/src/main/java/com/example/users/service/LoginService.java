package com.example.users.service;

import com.example.users.dto.authentication.AuthenticationDTO;
import com.example.users.dto.authentication.TokenJWTDTO;
import com.example.users.model.User;
import com.example.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private AuthenticationManager manager;


    /**
     * Method to return a token in the body when login is authenticated
     * @param dto AuthenticationDTO with login and password values
     * @return TokenJWTDTO
     */
    public TokenJWTDTO login(AuthenticationDTO dto) {
    var authenticationToken = new UsernamePasswordAuthenticationToken(dto.user(), dto.password());
    var authentication = manager.authenticate(authenticationToken);
    var tokenJWT = tokenService.createToken((User) authentication.getPrincipal());
        return new TokenJWTDTO(tokenJWT);
    }

}
