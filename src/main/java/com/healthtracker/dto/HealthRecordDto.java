package com.healthtracker.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.healthtracker.dto.UserSummaryDto;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HealthRecordDto {
    private Long id;
    @NotNull(message = "BP Systolic is required")
    private Integer bpSystolic;
    @NotNull(message = "BP Diastolic is required")
    private Integer bpDiastolic;
    private BigDecimal sugarLevel;
    private BigDecimal weight;
    @NotNull(message = "Heart Rate is required")
    private Integer heartRate;
    @NotNull(message = "Date is required")
    private LocalDate date;
    private String notes;

    private UserSummaryDto user;
}