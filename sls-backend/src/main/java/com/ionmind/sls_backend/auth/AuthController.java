package com.ionmind.sls_backend.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    record SignupRequest(String username, String password, String role) {}
    record LoginRequest(String username, String password) {}
    record LoginResponse(String token, String username, String role) {}

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest req) {
        try {
            Role role = Role.valueOf(req.role.toUpperCase());
            User u = authService.signup(req.username, req.password, role);
            return ResponseEntity.ok(u);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            String token = authService.login(req.username, req.password);
            User u = userRepository.findByUsername(req.username).get();
            return ResponseEntity.ok(new LoginResponse(token, u.getUsername(), u.getRole().name()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(name = "X-Auth-Token", required = false) String token) {
        authService.logout(token);
        return ResponseEntity.ok("Logged out");
    }
}
