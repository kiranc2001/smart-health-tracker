package com.healthtracker.controller;

import com.healthtracker.dto.AlertDto;
import com.healthtracker.dto.HealthAdviceRequestDto;
import com.healthtracker.model.Alert;
import com.healthtracker.model.User;
import com.healthtracker.service.AlertService;
import com.healthtracker.service.UserService;  // New import
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AlertController {

    private final AlertService alertService;
    private final UserService userService;  // New

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AlertDto>> getAlerts(@PathVariable Long userId) {
        User user = userService.findById(userId);  // Fixed
        List<AlertDto> alerts = alertService.getAlertsByUser(user);
        return ResponseEntity.ok(alerts);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<AlertDto> markRead(@PathVariable Long id, @RequestParam Long userId) {
        User user = userService.findById(userId);  // Fixed
        AlertDto alert = alertService.markAsRead(id, user);
        return ResponseEntity.ok(alert);
    }

    @PostMapping("/advice")
    public ResponseEntity<String> getAdvice(@RequestBody HealthAdviceRequestDto request) {
        String advice = alertService.getAiAdvice(request);
        return ResponseEntity.ok(advice);
    }

    @PostMapping("/generate/{userId}")
    public ResponseEntity<Void> generate(@PathVariable Long userId) {
        User user = userService.findById(userId);  // Fixed
        alertService.generateAlerts(user);
        return ResponseEntity.ok().build();
    }
}