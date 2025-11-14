package com.healthtracker.dto;

import lombok.Data;
import java.util.Map;

@Data
public class HealthAdviceRequestDto {
    private Long userId;
    private Map<String, Object> latestReadings; // e.g., {"bp": "120/80", "sugar": 100}
}