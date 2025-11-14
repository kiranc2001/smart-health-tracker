package com.healthtracker.controller;

import com.healthtracker.dto.*;
import com.healthtracker.model.User;
import com.healthtracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin("*") // For React
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserRequestDto request) {
        User user = userService.register(request);
        UserDto dto = modelMapper.map(user, UserDto.class);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody LoginRequestDto request) {
        System.out.println("DEBUG: Login controller called with email: " + request.getEmail());  // Temp
        User user = userService.login(request.getEmail(), request.getPassword());
        UserDto dto = modelMapper.map(user, UserDto.class);
        System.out.println("DEBUG: Login response mapped successfully");  // Temp
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        UserDto dto = modelMapper.map(user, UserDto.class);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDto request) {
        User user = userService.update(id, request);
        UserDto dto = modelMapper.map(user, UserDto.class);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> sendOtp(@Valid @RequestBody OtpRequestDto request) {
        userService.sendOtp(request.getEmail());
        return ResponseEntity.ok("OTP sent to email");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto request) {
        userService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword()); // Email from DTO? Add to DTO if needed
        return ResponseEntity.ok("Password reset successful");
    }
}