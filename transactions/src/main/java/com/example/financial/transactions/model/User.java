package com.example.financial.transactions.model;


import com.example.financial.transactions.dto.UserDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "username")
    private String name;
    private String email;
    private String status;


    public User(UserDto dto) {
        this.id = dto.id();
        this.name = dto.name();
        this.email = dto.email();
        this.status = dto.status();
    }
}
