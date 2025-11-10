package com.ionmind.sls_backend.auth;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "APP_USER")
@Data
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String passwordHash; // BCrypt hash

    @Enumerated(EnumType.STRING)
    private Role role;
}
