package com.healthtracker.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class OtpRequestDto {
    @Email(message = "Invalid email")
    private String email;
}