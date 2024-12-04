package com.example.security.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.security.kafka.consumer.KafkaConsumer;
import com.example.security.service.KafkaAuthenticationService;
import com.example.security.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private KafkaAuthenticationService kafkaAuthService;

    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var tokenJWT = recoverToken(request);

        if (tokenJWT != null) {
            logger.debug("Token found: {}", tokenJWT);
            try {
                var subject = tokenService.getSubject(tokenJWT);
                logger.debug("Token subject: {}", subject);

                var user = kafkaAuthService.getUserAuthentication(subject);
                if (user != null) {
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("User authenticated: {}", user.getName());
                } else {
                    logger.debug("User not found for subject: {}", subject);
                }
            } catch (JWTVerificationException e) {
                logger.error("Token verification failed: {}", e.getMessage());
            }
        } else {
            logger.debug("No token found in request");
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
