package com.ionmind.sls_backend.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Data
public class LoanRequestDto {
    @NotNull
    private Long equipmentId;

    @NotBlank
    private String requesterName;

    @Min(1)
    private int quantity;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private String note;
}
