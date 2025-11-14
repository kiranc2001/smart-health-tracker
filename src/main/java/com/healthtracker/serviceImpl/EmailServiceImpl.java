package com.healthtracker.serviceImpl;

import com.healthtracker.model.HealthRecord;
import com.healthtracker.model.User;
import com.healthtracker.repository.HealthRecordRepository;
import com.healthtracker.repository.UserRepository;
import com.healthtracker.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    @Autowired
    private UserRepository userRepository;  // New: For fetching all users

    @Override
    public void sendOtpEmail(User user, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Health Tracker - OTP for Password Reset");
        message.setText("Your OTP is: " + otp + ". Valid for 5 minutes.");
        mailSender.send(message);
    }

    @Override
    public void sendAlertEmail(User user, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Health Alert");
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }

    @Override
    @Scheduled(cron = "0 0 9 * * MON") // Weekly on Monday 9AM
    public void sendWeeklySummary() { // Fixed: No-arg method
        List<User> users = userRepository.findAll(); // Fetch all users
        for (User user : users) {
            try {
                List<HealthRecord> weekly = healthRecordRepository.findByUserOrderByDateDesc(user).stream()
                        .filter(r -> r.getDate() != null && r.getDate().isAfter(LocalDate.now().minusWeeks(1)))
                        .collect(Collectors.toList());

                if (weekly.isEmpty()) {
                    continue; // Skip if no records
                }

                StringBuilder body = new StringBuilder("Weekly Health Summary for " + user.getName() + ":\n");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                for (HealthRecord r : weekly) {
                    body.append("Date: ").append(sdf.format(r.getDate()))
                            .append(", BP: ").append(r.getBpSystolic()).append("/").append(r.getBpDiastolic() != null ? r.getBpDiastolic() : "N/A")
                            .append(", Sugar: ").append(r.getSugarLevel() != null ? r.getSugarLevel() : "N/A")
                            .append(", Weight: ").append(r.getWeight() != null ? r.getWeight() : "N/A")
                            .append(", Heart Rate: ").append(r.getHeartRate()).append("\n");
                }

                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(user.getEmail());
                message.setSubject("Weekly Health Report");
                message.setText(body.toString());
                mailSender.send(message);
            } catch (Exception e) {
                // Log error but don't stop scheduler (e.g., via SLF4J; add logger if needed)
                System.err.println("Failed to send weekly summary to " + user.getEmail() + ": " + e.getMessage());
            }
        }
    }
}