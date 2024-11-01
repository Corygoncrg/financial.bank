package com.example.financial.transactions.dto;


import com.example.financial.transactions.model.User;

public record UserDto(Long id, String name, String email, String status) {


    public UserDto(User d) {
        this(d.getId(), d.getName(), d.getEmail(), String.valueOf(d.getStatus()));
    }

}
