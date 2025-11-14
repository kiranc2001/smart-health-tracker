package com.healthtracker.service;

import com.healthtracker.dto.UserRequestDto;
import com.healthtracker.model.User;

public interface UserService {
    User register(UserRequestDto request);
    User login(String email, String password);
    User findById(Long id);
    User update(Long id, UserRequestDto request);
    void sendOtp(String email);
    void resetPassword(String email, String otp, String newPassword);
}