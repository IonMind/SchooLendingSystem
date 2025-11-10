package com.ionmind.sls_backend.auth;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    private final UserRepository userRepository;

    // token -> username
    private final Map<String, String> tokenStore = new ConcurrentHashMap<>();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User signup(String username, String password, Role role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        User u = new User();
        u.setUsername(username);
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(10));
        u.setPasswordHash(hash);
        u.setRole(role);
        return userRepository.save(u);
    }

    public String login(String username, String password) {
        Optional<User> uopt = userRepository.findByUsername(username);
        if (uopt.isEmpty()) throw new IllegalArgumentException("Invalid credentials");
        User u = uopt.get();
        if (!BCrypt.checkpw(password, u.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, username);
        return token;
    }

    public Optional<User> getUserByToken(String token) {
        if (token == null) return Optional.empty();
        String username = tokenStore.get(token);
        if (username == null) return Optional.empty();
        return userRepository.findByUsername(username);
    }

    public boolean hasRole(String token, Role expected) {
        return getUserByToken(token).map(u -> u.getRole() == expected).orElse(false);
    }

    public void logout(String token) {
        tokenStore.remove(token);
    }
}
