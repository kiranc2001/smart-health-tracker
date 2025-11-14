package com.healthtracker.service;

import com.healthtracker.model.User;

public interface EmailService {
    void sendWeeklySummary();
    void sendOtpEmail(User user, String otp);
    void sendAlertEmail(User user, String message);
}