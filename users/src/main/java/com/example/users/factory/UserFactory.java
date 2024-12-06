package com.example.users.factory;

import com.example.shared.model.User;
import com.example.shared.model.UserStatus;
import com.example.users.dto.user.UserRegisterDto;
import com.example.users.dto.user.UserUpdateDto;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class UserFactory {

    public User createUser(UserRegisterDto dto) {
        String generatedPassword = String.format("%06d", new Random().nextInt(999999));
        return new User(dto.username(), dto.email(), generatedPassword, UserStatus.PENDING);
    }

    public void updateUser(User user, UserUpdateDto dto) {
        user.update(dto.name(), dto.email(), dto.status());
    }
}
