package com.example.users.controller;

import com.example.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    UserRepository repository;

    @GetMapping("/users")
    private String hi() {
        return repository.findAll().toString();
    }
}
