package com.ionmind.sls_backend.controller;

import com.ionmind.sls_backend.dto.LoanRequestDto;
import com.ionmind.sls_backend.model.LoanRequest;
import com.ionmind.sls_backend.service.LoanService;
import org.springframework.http.ResponseEntity;
import com.ionmind.sls_backend.auth.AuthService;
import com.ionmind.sls_backend.auth.Role;
import com.ionmind.sls_backend.auth.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/loans")
@Validated
public class LoanController {
    private final LoanService loanService;

    private final AuthService authService;

    public LoanController(LoanService loanService, AuthService authService) {
        this.loanService = loanService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<?> createRequest(@Valid @RequestBody LoanRequestDto dto) {
        try {
            LoanRequest lr = loanService.createRequest(dto);
            return ResponseEntity.ok(lr);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public List<LoanRequest> listAll() {
        return loanService.listAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanRequest> getById(@PathVariable Long id) {
        return loanService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approve(@RequestHeader(name="X-Auth-Token", required=false) String token, @PathVariable Long id) {
        var userOpt = authService.getUserByToken(token);
        if (userOpt.isEmpty()) return ResponseEntity.status(401).body("Unauthorized");
        User user = userOpt.get();
        if (user.getRole() != Role.ADMIN && user.getRole() != Role.STAFF) {
            return ResponseEntity.status(403).body("Only staff/admin can approve");
        }
        try {
            return ResponseEntity.ok(loanService.approveRequest(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(@RequestHeader(name="X-Auth-Token", required=false) String token, @PathVariable Long id, @RequestBody(required = false) String reason) {
        var userOpt = authService.getUserByToken(token);
        if (userOpt.isEmpty()) return ResponseEntity.status(401).body("Unauthorized");
        User user = userOpt.get();
        if (user.getRole() != Role.ADMIN && user.getRole() != Role.STAFF) {
            return ResponseEntity.status(403).body("Only staff/admin can reject");
        }
        try {
            return ResponseEntity.ok(loanService.rejectRequest(id, reason));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<?> markReturned(@RequestHeader(name="X-Auth-Token", required=false) String token, @PathVariable Long id) {
        var userOpt = authService.getUserByToken(token);
        if (userOpt.isEmpty()) return ResponseEntity.status(401).body("Unauthorized");
        User user = userOpt.get();
        if (user.getRole() != Role.ADMIN && user.getRole() != Role.STAFF) {
            return ResponseEntity.status(403).body("Only staff/admin can mark returned");
        }
        try {
            return ResponseEntity.ok(loanService.markReturned(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
