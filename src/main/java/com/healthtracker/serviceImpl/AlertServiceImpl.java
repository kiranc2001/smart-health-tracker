package com.healthtracker.serviceImpl;

import com.healthtracker.dto.AlertDto;
import com.healthtracker.dto.HealthAdviceRequestDto;
import com.healthtracker.dto.UserSummaryDto;
import com.healthtracker.exception.AiServiceException;
import com.healthtracker.exception.ResourceNotFoundException;
import com.healthtracker.model.Alert;
import com.healthtracker.model.HealthRecord;
import com.healthtracker.model.User;
import com.healthtracker.repository.AlertRepository;
import com.healthtracker.repository.HealthRecordRepository;
import com.healthtracker.service.AlertService;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;

@Service
public class AlertServiceImpl implements AlertService {

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    @Value("${openai.api.key}")
    private String openAiKey;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void generateAlerts(User user) {
        // Rule-based alerts
        List<HealthRecord> recent = healthRecordRepository.findByUserOrderByDateDesc(user)
                .stream().limit(1).collect(Collectors.toList());

        if (recent.isEmpty()) return;

        HealthRecord latest = recent.get(0);
        List<Alert> alerts = new ArrayList<>();

        if (latest.getBpSystolic() != null && latest.getBpSystolic() > 140) {
            alerts.add(createAlert(user, "High BP detected: Consult doctor."));
        }
        if (latest.getSugarLevel() != null && latest.getSugarLevel().compareTo(BigDecimal.valueOf(126)) > 0) {
            alerts.add(createAlert(user, "Elevated sugar level: Monitor diet."));
        }
        if (latest.getHeartRate() != null && latest.getHeartRate() > 100) {
            alerts.add(createAlert(user, "High heart rate: Rest and hydrate."));
        }

        // AI-based advice
        try {
            HealthAdviceRequestDto aiReq = new HealthAdviceRequestDto();
            aiReq.setUserId(user.getId());
            Integer diastolic = latest.getBpDiastolic() != null ? latest.getBpDiastolic() : 0;
            String bp = latest.getBpSystolic() + "/" + diastolic;
            aiReq.setLatestReadings(Map.of(
                    "bp", bp,
                    "sugar", latest.getSugarLevel(),
                    "weight", latest.getWeight(),
                    "heart_rate", latest.getHeartRate()
            ));
            String aiAdvice = getAiAdvice(aiReq);
            alerts.add(createAlert(user, "AI Suggestion: " + aiAdvice));
        } catch (Exception e) {
            throw new AiServiceException("Failed to generate AI advice: " + e.getMessage());
        }

        alertRepository.saveAll(alerts);
    }

    @Override
    public List<AlertDto> getAlertsByUser(User user) {
        List<Alert> alerts = alertRepository.findByUserOrderByCreatedAtDesc(user);
        return alerts.stream().map(a -> {
            AlertDto dto = modelMapper.map(a, AlertDto.class);
            dto.setUser(modelMapper.map(a.getUser(), UserSummaryDto.class));  // Manual set sanitized user
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public AlertDto markAsRead(Long id, User user) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found: " + id));
        if (!alert.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        alert.setRead(true);
        Alert updated = alertRepository.save(alert);
        AlertDto dto = modelMapper.map(updated, AlertDto.class);
        dto.setUser(modelMapper.map(updated.getUser(), UserSummaryDto.class));  // Manual
        return dto;
    }

    @Override
    public String getAiAdvice(HealthAdviceRequestDto request) {
        try {
            // Create OpenAI client with API key
            OpenAIClient client = OpenAIOkHttpClient.builder()
                    .apiKey(openAiKey)
                    .build();

            // Build prompt
            String readings = request.getLatestReadings() != null ? request.getLatestReadings().toString() : "{}";
            String prompt = "You are a health advisor. Based on user's latest readings: " + readings +
                    ". Provide 3 concise, safe suggestions. Do not give medical advice; " +
                    "suggest consulting a doctor if serious.";

            // Build Chat request params
            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                    .model(ChatModel.GPT_4O_MINI)
                    .addUserMessage(prompt)
                    .maxTokens(150)
                    .build();

            // Create completion
            ChatCompletion response = client.chat().completions().create(params);

            // Extract content safely (handle empty choices/list)
            if (response.choices() == null || response.choices().isEmpty() ||
                    response.choices().get(0).message() == null ||
                    response.choices().get(0).message().content() == null ||
                    response.choices().get(0).message().content().isEmpty()) {
                return "No advice generated. Please try again.";
            }

            // Join text from all parts (usually one string)
            String content = response.choices().get(0).message().content().stream()
                    .collect(Collectors.joining("\n"));

            return content.isEmpty() ? "No content available." : content.trim();

        } catch (Exception e) {
            throw new AiServiceException("OpenAI service error: " + e.getMessage());
        }
    }

    // Helper method
    private Alert createAlert(User user, String message) {
        Alert alert = new Alert();
        alert.setUser(user);
        alert.setMessage(message);
        alert.setCreatedAt(LocalDateTime.now());
        return alert;
    }
}