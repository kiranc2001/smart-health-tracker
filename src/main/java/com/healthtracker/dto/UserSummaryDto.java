package com.healthtracker.dto;

import lombok.Data;

@Data
public class UserSummaryDto {
    private Long id;
    private String name;
    private String email;
    // No passwordHash, createdAt
}