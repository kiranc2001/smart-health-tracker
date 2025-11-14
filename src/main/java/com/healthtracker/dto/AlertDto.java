package com.healthtracker.dto;

import lombok.Data;

@Data
public class AlertDto {
    private Long id;
    private String message;
    private Boolean read;

    private UserSummaryDto user;
}