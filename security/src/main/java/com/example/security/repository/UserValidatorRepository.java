package com.example.security.repository;


import com.example.shared.model.UserValidator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserValidatorRepository extends JpaRepository<UserValidator, Long> {

    Optional<com.example.shared.model.UserValidator> findByUuid (String uuid);
}
