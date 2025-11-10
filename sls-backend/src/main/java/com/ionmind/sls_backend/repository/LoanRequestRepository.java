package com.ionmind.sls_backend.repository;

import com.ionmind.sls_backend.model.LoanRequest;
import com.ionmind.sls_backend.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRequestRepository extends JpaRepository<LoanRequest, Long> {
    List<LoanRequest> findByStatus(String status);

    // find approved requests for a particular equipment that overlap a date range
    @Query("select lr from LoanRequest lr where lr.equipment = :equipment and lr.status = 'APPROVED' and not (lr.endDate < :start or lr.startDate > :end)")
    List<LoanRequest> findApprovedOverlapping(@Param("equipment") Equipment equipment, @Param("start") LocalDate start, @Param("end") LocalDate end);
}
