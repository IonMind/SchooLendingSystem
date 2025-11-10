package com.ionmind.sls_backend.controller;

import com.ionmind.sls_backend.model.Equipment;
import com.ionmind.sls_backend.service.EquipmentService;
import org.springframework.http.ResponseEntity;
import com.ionmind.sls_backend.auth.AuthService;
import com.ionmind.sls_backend.auth.Role;
import com.ionmind.sls_backend.auth.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.ionmind.sls_backend.exception.ForbiddenException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {
    private final EquipmentService equipmentService;

    private final AuthService authService;

    public EquipmentController(EquipmentService equipmentService, AuthService authService) {
        this.equipmentService = equipmentService;
        this.authService = authService;
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
    public ResponseEntity<?> create(@RequestHeader(name="X-Auth-Token", required=false) String token, @RequestBody Equipment equipment) {
        if (!authService.hasRole(token, Role.ADMIN)) {
            throw new ForbiddenException("Only ADMIN can create equipment");
        }
        Equipment saved = equipmentService.save(equipment);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader(name="X-Auth-Token", required=false) String token, @PathVariable Long id, @RequestBody Equipment payload) {
        if (!authService.hasRole(token, Role.ADMIN)) {
            throw new ForbiddenException("Only ADMIN can update equipment");
        }
        return equipmentService.findById(id).map(e -> {
            e.setName(payload.getName());
            e.setCategory(payload.getCategory());
            e.setConditionDesc(payload.getConditionDesc());
            e.setTotalQuantity(payload.getTotalQuantity());
            return ResponseEntity.ok(equipmentService.save(e));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestHeader(name="X-Auth-Token", required=false) String token, @PathVariable Long id) {
        if (!authService.hasRole(token, Role.ADMIN)) {
            throw new ForbiddenException("Only ADMIN can delete equipment");
        }
        equipmentService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public List<Equipment> search(@RequestParam("category") String category) {
        return equipmentService.searchByCategory(category);
    }
}
