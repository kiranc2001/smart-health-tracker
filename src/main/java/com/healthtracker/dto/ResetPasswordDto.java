package com.healthtracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordDto {
    @NotBlank
    private String otp;
    @NotBlank
    private String newPassword;
    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;
}