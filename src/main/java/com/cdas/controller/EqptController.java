package com.cdas.controller;

import com.cdas.entity.EqptMaster;
import com.cdas.repository.EqptMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eqpt")
@CrossOrigin("*")
public class EqptController {

    @Autowired
    private EqptMasterRepository repository;

    // ─── GET ALL SHOPS (full table) ───────────────────────────────
    @GetMapping("/shops")
    public List<EqptMaster> getAllShops() {
        return repository.findAll();
    }

    // ─── GET EQUIPMENTS BY SHOP CODE ─────────────────────────────
    @GetMapping("/{shopCode}")
    public List<EqptMaster> getEquipments(@PathVariable String shopCode) {
        return repository.getByShopCode(shopCode);
    }

    // ─── POST — CREATE SINGLE EQUIPMENT RECORD ───────────────────
    @PostMapping
    public ResponseEntity<EqptMaster> createEquipment(@RequestBody EqptMaster eqpt) {
        EqptMaster saved = repository.save(eqpt);
        return ResponseEntity.ok(saved);
    }

    // ─── POST — BULK IMPORT FROM CSV ─────────────────────────────
    @PostMapping("/bulk")
    public ResponseEntity<String> bulkImport(@RequestBody List<EqptMaster> eqpts) {
        List<EqptMaster> saved = repository.saveAll(eqpts);
        return ResponseEntity.ok(saved.size() + " records imported successfully.");
    }
}