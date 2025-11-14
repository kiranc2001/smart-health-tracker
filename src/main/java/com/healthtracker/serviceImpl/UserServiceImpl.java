package com.healthtracker.serviceImpl;

import com.healthtracker.dto.UserRequestDto;
import com.healthtracker.exception.AuthenticationException;
import com.healthtracker.exception.DuplicateResourceException;
import com.healthtracker.exception.InvalidOtpException;
import com.healthtracker.exception.ResourceNotFoundException;
import com.healthtracker.helper.OtpHelper;
import com.healthtracker.model.User;
import com.healthtracker.repository.UserRepository;
import com.healthtracker.service.EmailService;
import com.healthtracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // @Bean in config

    // In-memory OTP store (for personal project; use Redis in prod)
    private final Map<String, String> otpStore = new ConcurrentHashMap<>(); // email -> otp

    @Override
    public User register(UserRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already exists");  // Changed: Custom 409
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User login(String email, String password) {
        System.out.println("DEBUG: Login attempt for email: " + email);  // Temp log
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("DEBUG: User not found for " + email);  // Temp
                    return new ResourceNotFoundException("User not found");
                });
        System.out.println("DEBUG: User found, checking password...");
        // Temp
        if (user.getPasswordHash() == null) {
            throw new AuthenticationException("User password not set");
        }
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            System.out.println("DEBUG: Password mismatch");  // Temp
            throw new AuthenticationException("Invalid credentials");
        }
        System.out.println("DEBUG: Login successful for " + user.getId());  // Temp
        return user;
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    @Override
    public User update(Long id, UserRequestDto request) {
        User user = findById(id);
        user.setName(request.getName());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    public void sendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String otp = OtpHelper.generateOtp();
        otpStore.put(email, otp);
        emailService.sendOtpEmail(user, otp);
        // OTP expires in 5 min (add timer in prod)
    }

    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        if (!otpStore.getOrDefault(email, "").equals(otp)) {
            throw new InvalidOtpException("Invalid OTP");
        }
        User user = findById(userRepository.findByEmail(email).get().getId());
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        otpStore.remove(email); // Clear OTP
    }
}