package com.healthtracker.controller;

import com.healthtracker.dto.HealthRecordDto;
import com.healthtracker.model.HealthRecord;
import com.healthtracker.model.User;
import com.healthtracker.service.HealthRecordService;
import com.healthtracker.service.UserService;  // New import
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.healthtracker.helper.PdfExportHelper;

import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
@CrossOrigin("*")
public class HealthRecordController {

    private final HealthRecordService healthRecordService;
    private final PdfExportHelper pdfExportHelper;
    private final UserService userService;  // New: Inject for real user fetch

    @PostMapping
    public ResponseEntity<HealthRecordDto> create(@Valid @RequestBody HealthRecordDto dto, @RequestParam Long userId) {
        User user = userService.findById(userId);  // Fixed: Fetch real managed User
        HealthRecordDto record = healthRecordService.create(dto, user);
        return ResponseEntity.ok(record);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HealthRecordDto> update(@PathVariable Long id, @Valid @RequestBody HealthRecordDto dto, @RequestParam Long userId) {
        User user = userService.findById(userId);  // Fixed
        HealthRecordDto record = healthRecordService.update(id, dto, user);
        return ResponseEntity.ok(record);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam Long userId) {
        User user = userService.findById(userId);  // Fixed
        healthRecordService.delete(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<HealthRecordDto>> getAllByUser(@PathVariable Long userId) {
        User user = userService.findById(userId);  // Fixed (for consistency)
        List<HealthRecordDto> records = healthRecordService.getAllByUser(user);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/user/{userId}/date")
    public ResponseEntity<List<HealthRecordDto>> getByDate(@PathVariable Long userId, @RequestParam String date) {
        User user = userService.findById(userId);  // Fixed
        List<HealthRecordDto> records = healthRecordService.getByDate(user, java.time.LocalDate.parse(date));
        return ResponseEntity.ok(records);
    }

    // Exports (no change needed, but fetch user for auth)
    @GetMapping(value = "/user/{userId}/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long userId) throws Exception {
        User user = userService.findById(userId);  // Fixed
        List<HealthRecordDto> records = healthRecordService.exportByUser(user);
        byte[] pdfBytes = pdfExportHelper.generatePdf(records);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=health_records.pdf");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping(value = "/user/{userId}/export/csv", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> exportCsv(@PathVariable Long userId) {
        User user = userService.findById(userId);  // Fixed
        List<HealthRecordDto> records = healthRecordService.exportByUser(user);
        StringBuilder csv = new StringBuilder("Date,BP,Sugar,Weight,Heart Rate,Notes\n");
        for (HealthRecordDto r : records) {
            csv.append(r.getDate()).append(",")
                    .append(r.getBpSystolic()).append("/").append(r.getBpDiastolic()).append(",")
                    .append(r.getSugarLevel()).append(",")
                    .append(r.getWeight()).append(",")
                    .append(r.getHeartRate()).append(",")
                    .append(r.getNotes() != null ? r.getNotes().replace(",", ";") : "").append("\n");
        }
        byte[] csvBytes = csv.toString().getBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=health_records.csv");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(csvBytes);
    }
}