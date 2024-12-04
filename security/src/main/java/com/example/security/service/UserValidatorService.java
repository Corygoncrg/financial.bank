package com.example.security.service;

import com.example.security.repository.UserValidatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserValidatorService {

    @Autowired
    UserValidatorRepository repository;

}
