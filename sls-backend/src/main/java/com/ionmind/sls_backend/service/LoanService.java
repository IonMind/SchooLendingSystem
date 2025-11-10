package com.ionmind.sls_backend.service;

import com.ionmind.sls_backend.dto.LoanRequestDto;
import com.ionmind.sls_backend.model.Equipment;
import com.ionmind.sls_backend.model.LoanRequest;
import com.ionmind.sls_backend.repository.EquipmentRepository;
import com.ionmind.sls_backend.repository.LoanRequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {
    private final LoanRequestRepository loanRequestRepository;
    private final EquipmentRepository equipmentRepository;

    public LoanService(LoanRequestRepository loanRequestRepository, EquipmentRepository equipmentRepository) {
        this.loanRequestRepository = loanRequestRepository;
        this.equipmentRepository = equipmentRepository;
    }

    public LoanRequest createRequest(LoanRequestDto dto) {
        Optional<Equipment> eqOpt = equipmentRepository.findById(dto.getEquipmentId());
        if (!eqOpt.isPresent()) throw new IllegalArgumentException("Equipment not found");

        Equipment equipment = eqOpt.get();
        LoanRequest lr = new LoanRequest();
        lr.setEquipment(equipment);
        lr.setRequesterName(dto.getRequesterName());
        lr.setQuantity(dto.getQuantity());
        lr.setStartDate(dto.getStartDate());
        lr.setEndDate(dto.getEndDate());
        lr.setStatus("PENDING");
        lr.setNote(dto.getNote());

        // Basic validation
        if (lr.getStartDate() == null || lr.getEndDate() == null || lr.getStartDate().isAfter(lr.getEndDate())) {
            throw new IllegalArgumentException("Invalid start/end date");
        }
        if (lr.getQuantity() <= 0) throw new IllegalArgumentException("Quantity must be positive");

        return loanRequestRepository.save(lr);
    }

    public List<LoanRequest> listAll() {
        return loanRequestRepository.findAll();
    }

    public Optional<LoanRequest> findById(Long id) {
        return loanRequestRepository.findById(id);
    }

    public LoanRequest approveRequest(Long requestId) {
        LoanRequest lr = loanRequestRepository.findById(requestId).orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (!"PENDING".equals(lr.getStatus())) {
            throw new IllegalStateException("Only PENDING requests can be approved");
        }

        // Check overlapping approved requests for same equipment
        Equipment equipment = lr.getEquipment();
        LocalDate start = lr.getStartDate();
        LocalDate end = lr.getEndDate();

        List<LoanRequest> overlaps = loanRequestRepository.findApprovedOverlapping(equipment, start, end);
        int alreadyBooked = overlaps.stream().mapToInt(LoanRequest::getQuantity).sum();

        if (alreadyBooked + lr.getQuantity() > equipment.getTotalQuantity()) {
            throw new IllegalStateException("Not enough quantity available for the requested date range");
        }

        lr.setStatus("APPROVED");
        return loanRequestRepository.save(lr);
    }

    public LoanRequest rejectRequest(Long requestId, String reason) {
        LoanRequest lr = loanRequestRepository.findById(requestId).orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (!"PENDING".equals(lr.getStatus())) {
            throw new IllegalStateException("Only PENDING requests can be rejected");
        }
        lr.setStatus("REJECTED");
        lr.setNote(reason);
        return loanRequestRepository.save(lr);
    }

    public LoanRequest markReturned(Long requestId) {
        LoanRequest lr = loanRequestRepository.findById(requestId).orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (!"APPROVED".equals(lr.getStatus())) {
            throw new IllegalStateException("Only APPROVED loans can be marked returned");
        }
        lr.setStatus("RETURNED");
        return loanRequestRepository.save(lr);
    }
}
