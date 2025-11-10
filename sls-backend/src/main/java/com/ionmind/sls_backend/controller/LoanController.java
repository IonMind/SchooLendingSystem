package com.ionmind.sls_backend.controller;

import com.ionmind.sls_backend.dto.LoanRequestDto;
import com.ionmind.sls_backend.model.LoanRequest;
import com.ionmind.sls_backend.service.LoanService;
import org.springframework.http.ResponseEntity;
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

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
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
    public ResponseEntity<?> approve(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(loanService.approveRequest(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id, @RequestBody(required = false) String reason) {
        try {
            return ResponseEntity.ok(loanService.rejectRequest(id, reason));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<?> markReturned(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(loanService.markReturned(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
