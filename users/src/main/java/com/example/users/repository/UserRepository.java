package com.example.users.repository;

import com.example.shared.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.id <> 1 AND u.status <> 'PENDING'")
    List<User> findAllUsers();

    boolean existsByName(String username);

    boolean existsByEmail(String email);

    User findByName(String username);

    User findByEmail(String email);
}
