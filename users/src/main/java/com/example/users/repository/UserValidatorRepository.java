package com.example.users.repository;

import com.example.users.model.UserValidator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserValidatorRepository extends JpaRepository<UserValidator, Long> {

    Optional<UserValidator> findByUuid (String uuid);
}
