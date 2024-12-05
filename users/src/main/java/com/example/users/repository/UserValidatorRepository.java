package com.example.users.repository;

import com.example.shared.model.UserValidator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserValidatorRepository extends JpaRepository<UserValidator, Long> {

    Optional<UserValidator> findByUuid (String uuid);
}
