package com.healthtracker.helper;

import java.security.SecureRandom;
import java.util.Random;

public class OtpHelper {
    private static final Random random = new SecureRandom();
    private static final int OTP_LENGTH = 6;

    public static String generateOtp() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
}