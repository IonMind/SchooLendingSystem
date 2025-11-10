package com.ionmind.sls_backend.controller;

import com.ionmind.sls_backend.model.Equipment;
import com.ionmind.sls_backend.service.EquipmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {
    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping
    public List<Equipment> listAll() {
        return equipmentService.listAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipment> getById(@PathVariable Long id) {
        return equipmentService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Equipment create(@RequestBody Equipment equipment) {
        return equipmentService.save(equipment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Equipment> update(@PathVariable Long id, @RequestBody Equipment payload) {
        return equipmentService.findById(id).map(e -> {
            e.setName(payload.getName());
            e.setCategory(payload.getCategory());
            e.setConditionDesc(payload.getConditionDesc());
            e.setTotalQuantity(payload.getTotalQuantity());
            return ResponseEntity.ok(equipmentService.save(e));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        equipmentService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public List<Equipment> search(@RequestParam("category") String category) {
        return equipmentService.searchByCategory(category);
    }
}
