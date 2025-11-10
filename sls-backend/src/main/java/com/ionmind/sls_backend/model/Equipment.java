package com.ionmind.sls_backend.model;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Table(name = "EQUIPMENT")
@Data
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;
    private String conditionDesc;

    // total available quantity in store (fixed)
    private int totalQuantity;

    // units currently allocated (approved but not yet returned)
    private int allocatedQuantity;

    @Transient
    public int getAvailableQuantity() {
        return totalQuantity - allocatedQuantity;
    }
}
