package com.example.security.repository;


import com.example.shared.model.UserValidator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserValidatorRepository extends JpaRepository<UserValidator, Long> {

    Optional<UserValidator> findByUuid (String uuid);

    Optional<UserValidator> findByIdUser_Id(Long UserId);
}
