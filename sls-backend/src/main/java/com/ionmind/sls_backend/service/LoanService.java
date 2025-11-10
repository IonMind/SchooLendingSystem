package com.ionmind.sls_backend.service;

import com.ionmind.sls_backend.dto.LoanRequestDto;
import com.ionmind.sls_backend.model.Equipment;
import com.ionmind.sls_backend.model.LoanRequest;
import com.ionmind.sls_backend.repository.EquipmentRepository;
import com.ionmind.sls_backend.repository.LoanRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // Query methods first
    public List<LoanRequest> listAll() {
        return loanRequestRepository.findAll();
    }

    public Optional<LoanRequest> findById(Long id) {
        return loanRequestRepository.findById(id);
    }

    // Creation
    public LoanRequest createRequest(LoanRequestDto dto) {
        Optional<Equipment> equipmentOpt = equipmentRepository.findById(dto.getEquipmentId());
        if (!equipmentOpt.isPresent()) throw new IllegalArgumentException("Equipment not found");

        Equipment equipment = equipmentOpt.get();
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setEquipment(equipment);
        loanRequest.setRequesterName(dto.getRequesterName());
        loanRequest.setQuantity(dto.getQuantity());
        loanRequest.setStartDate(dto.getStartDate());
        loanRequest.setEndDate(dto.getEndDate());
        loanRequest.setStatus("PENDING");
        loanRequest.setNote(dto.getNote());

        // Basic validation
        if (loanRequest.getStartDate() == null || loanRequest.getEndDate() == null
                || loanRequest.getStartDate().isAfter(loanRequest.getEndDate())) {
            throw new IllegalArgumentException("Invalid start/end date");
        }
        if (loanRequest.getQuantity() <= 0) throw new IllegalArgumentException("Quantity must be positive");

        return loanRequestRepository.save(loanRequest);
    }

    // State transitions
    @Transactional
    public LoanRequest approveRequest(Long requestId) {
        LoanRequest loanRequest = loanRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (!"PENDING".equals(loanRequest.getStatus())) {
            throw new IllegalStateException("Only PENDING requests can be approved");
        }

        Equipment equipment = loanRequest.getEquipment();

        // capacity check against remaining available (total - allocated)
        if (equipment.getAllocatedQuantity() + loanRequest.getQuantity() > equipment.getTotalQuantity()) {
            throw new IllegalStateException("Not enough quantity available");
        }

        loanRequest.setStatus("APPROVED");
        equipment.setAllocatedQuantity(equipment.getAllocatedQuantity() + loanRequest.getQuantity());
        equipmentRepository.save(equipment);
        return loanRequestRepository.save(loanRequest);
    }

    public LoanRequest rejectRequest(Long requestId, String reason) {
        LoanRequest loanRequest = loanRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (!"PENDING".equals(loanRequest.getStatus())) {
            throw new IllegalStateException("Only PENDING requests can be rejected");
        }
        loanRequest.setStatus("REJECTED");
        loanRequest.setNote(reason);
        return loanRequestRepository.save(loanRequest);
    }

    @Transactional
    public LoanRequest markReturned(Long requestId) {
        LoanRequest loanRequest = loanRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (!"APPROVED".equals(loanRequest.getStatus())) {
            throw new IllegalStateException("Only APPROVED loans can be marked returned");
        }
        loanRequest.setStatus("RETURNED");

        Equipment equipment = loanRequest.getEquipment();
        int newAllocated = equipment.getAllocatedQuantity() - loanRequest.getQuantity();
        equipment.setAllocatedQuantity(Math.max(0, newAllocated));
        equipmentRepository.save(equipment);

        return loanRequestRepository.save(loanRequest);
    }
}
