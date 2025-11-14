package com.healthtracker.service;

import com.healthtracker.dto.AlertDto;
import com.healthtracker.dto.HealthAdviceRequestDto;
import com.healthtracker.model.Alert;
import com.healthtracker.model.User;
import java.util.List;

public interface AlertService {
    void generateAlerts(User user); // Rule-based + AI
    List<AlertDto> getAlertsByUser(User user);
    AlertDto markAsRead(Long id, User user);
    String getAiAdvice(HealthAdviceRequestDto request); // Returns AI response as string
}