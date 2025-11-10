package com.ionmind.sls_backend.repository;

import com.ionmind.sls_backend.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    List<Equipment> findByCategoryContainingIgnoreCase(String category);
}
