package com.example.users.repository;

import com.example.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.id <> 1 AND u.status <> 'PENDING'")
    List<User> findAllUsers();

    boolean existsByName(@Param("username") String username);

    boolean existsByEmail(String email);

    UserDetails findByName(String username);
}
