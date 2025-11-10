package com.ionmind.sls_backend.model;

import lombok.Data;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "LOAN_REQUEST")
@Data
public class LoanRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requesterName; // student/teacher name (skip auth)

    @ManyToOne
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    private int quantity;

    private LocalDate startDate;
    private LocalDate endDate;

    // Status: PENDING, APPROVED, REJECTED, RETURNED
    private String status;

    private String note;
}
