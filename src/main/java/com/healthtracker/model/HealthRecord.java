package com.healthtracker.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "health_records")
@Data
public class HealthRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "bp_systolic")
    private Integer bpSystolic;

    @Column(name = "bp_diastolic")
    private Integer bpDiastolic;

    @Column(name = "sugar_level", precision = 5, scale = 2)
    private BigDecimal sugarLevel;

    @Column(precision = 5, scale = 2)
    private BigDecimal weight;

    @Column(name = "heart_rate")
    private Integer heartRate;

    @Column(nullable = false)
    private LocalDate date;

    private String notes;
}