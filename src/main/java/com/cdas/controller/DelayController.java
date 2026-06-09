package com.cdas.controller;

import com.cdas.entity.DelayData;
import com.cdas.repository.DelayRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/delays")
@CrossOrigin("*")
public class DelayController {

    @Autowired
    DelayRepository repo;

    // ─── GET ALL DELAYS ─────────────────────────────────────────
    @GetMapping
    public List<DelayData> getAllDelays() {
        return repo.findAll();
    }

    // ─── GET DELAYS BY DEPARTMENT (dept-scoped access) ──────────
    @GetMapping("/dept/{dept}")
    public List<DelayData> getDelaysByDept(@PathVariable String dept) {
        return repo.findAll().stream()
                .filter(d -> dept.equalsIgnoreCase(d.getShopCode())
                        || (d.getShopDesc() != null && d.getShopDesc().equalsIgnoreCase(dept)))
                .collect(Collectors.toList());
    }

    // ─── GET DELAYS BY SHIFT ─────────────────────────────────────
    // shift = "A" (06:00–14:00), "B" (14:00–22:00), "C" (22:00–06:00)
    @GetMapping("/shift/{shift}")
    public ResponseEntity<?> getDelaysByShift(@PathVariable String shift) {
        String shiftUpper = shift.toUpperCase();
        if (!shiftUpper.matches("[ABC]")) {
            return ResponseEntity.badRequest()
                    .body("Invalid shift code. Use A, B, or C.");
        }
        List<DelayData> result = repo.findAll().stream()
                .filter(d -> shiftUpper.equals(resolveShift(d.getDelayFrom())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // ─── GET SHIFT SUMMARY (count + downtime per shift) ──────────
    @GetMapping("/shifts-summary")
    public ResponseEntity<Map<String, Object>> getShiftsSummary() {
        List<DelayData> all = repo.findAll();

        Map<String, Long>   counts   = new LinkedHashMap<>();
        Map<String, Double> downtimeHrs = new LinkedHashMap<>();
        Map<String, Map<String, Long>> agencySplit = new LinkedHashMap<>();

        for (String sh : new String[]{"A", "B", "C"}) {
            counts.put(sh, 0L);
            downtimeHrs.put(sh, 0.0);
            agencySplit.put(sh, new LinkedHashMap<>());
        }

        for (DelayData d : all) {
            String sh = resolveShift(d.getDelayFrom());
            counts.merge(sh, 1L, Long::sum);
            downtimeHrs.merge(sh, parseDuration(d.getDelayDuration()), Double::sum);
            if (d.getAgency() != null) {
                agencySplit.get(sh).merge(d.getAgency(), 1L, Long::sum);
            }
        }

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("shiftCounts",   counts);
        summary.put("shiftDowntime", downtimeHrs);
        summary.put("shiftAgency",   agencySplit);
        summary.put("totalDelays",   (long) all.size());

        return ResponseEntity.ok(summary);
    }

    // ─── GET SHOP × SHIFT HEATMAP ────────────────────────────────
    @GetMapping("/shop-shift-heatmap")
    public ResponseEntity<List<Map<String, Object>>> getShopShiftHeatmap() {
        List<DelayData> all = repo.findAll();

        // Group by shopCode → shift → count
        Map<String, Map<String, Long>> map = new LinkedHashMap<>();
        for (DelayData d : all) {
            String shop = d.getShopCode() != null ? d.getShopCode() : "UNKNOWN";
            String sh   = resolveShift(d.getDelayFrom());
            map.computeIfAbsent(shop, k -> new LinkedHashMap<>())
                    .merge(sh, 1L, Long::sum);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        map.forEach((shop, shiftCounts) -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("shopCode", shop);
            row.put("shopDesc", all.stream()
                    .filter(d -> shop.equals(d.getShopCode()))
                    .map(DelayData::getShopDesc).filter(Objects::nonNull)
                    .findFirst().orElse(shop));
            row.put("shiftA", shiftCounts.getOrDefault("A", 0L));
            row.put("shiftB", shiftCounts.getOrDefault("B", 0L));
            row.put("shiftC", shiftCounts.getOrDefault("C", 0L));
            row.put("total",  shiftCounts.values().stream().mapToLong(Long::longValue).sum());
            result.add(row);
        });

        // Sort by total desc
        result.sort((a, b) -> Long.compare((Long) b.get("total"), (Long) a.get("total")));
        return ResponseEntity.ok(result);
    }

    // ─── POST NEW DELAY ──────────────────────────────────────────
    @PostMapping
    public DelayData saveDelay(@RequestBody DelayData delay) {
        delay.setCreatedAt(LocalDateTime.now());
        return repo.save(delay);
    }

    // ─── PUT UPDATE DELAY ────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDelay(
            @PathVariable Long id,
            @RequestBody DelayData updated) {
        return repo.findById(id).map(existing -> {
            existing.setShopCode(updated.getShopCode());
            existing.setShopDesc(updated.getShopDesc());
            existing.setEqptName(updated.getEqptName());
            existing.setSubEqptName(updated.getSubEqptName());
            existing.setAgency(updated.getAgency());
            existing.setDelayFrom(updated.getDelayFrom());
            existing.setDelayUpto(updated.getDelayUpto());
            existing.setDelayDuration(updated.getDelayDuration());
            existing.setDelayDesc(updated.getDelayDesc());
            existing.setUserEntered(updated.getUserEntered());
            return ResponseEntity.ok(repo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ─── DELETE DELAY ────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDelay(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ─── PRIVATE HELPERS ────────────────────────────────────────

    /**
     * Determines shift code from a LocalDateTime.
     *   A = 06:00 – 13:59 (Morning)
     *   B = 14:00 – 21:59 (Afternoon)
     *   C = 22:00 – 05:59 (Night)
     */
    private String resolveShift(LocalDateTime dt) {
        if (dt == null) return "C";
        int hour = dt.getHour();
        if (hour >= 6  && hour < 14) return "A";
        if (hour >= 14 && hour < 22) return "B";
        return "C";
    }

    /**
     * Parses "HH:MM" or "HH:MM:SS" duration strings to decimal hours.
     * Returns 0.0 on null / unparseable input.
     */
    private double parseDuration(String dur) {
        if (dur == null || dur.isBlank()) return 0.0;
        try {
            String[] parts = dur.split(":");
            double hours   = Double.parseDouble(parts[0]);
            double minutes = parts.length > 1 ? Double.parseDouble(parts[1]) : 0;
            return hours + minutes / 60.0;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}