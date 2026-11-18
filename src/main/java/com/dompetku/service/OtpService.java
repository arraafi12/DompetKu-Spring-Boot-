package com.dompetku.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    private final SecureRandom random = new SecureRandom();

    private final Map<String, OtpData> otpStorage =
            new ConcurrentHashMap<>();

    public String generateOtp(String email) {
        String otp = String.format("%06d",
                random.nextInt(1_000_000));

        LocalDateTime expiry = LocalDateTime.now()
                .plusMinutes(5);

        otpStorage.put(email.toLowerCase(),
                new OtpData(otp, expiry));

        System.out.println("================================");
        System.out.println("OTP PEMULIHAN DOMPETKU");
        System.out.println("Email: " + email);
        System.out.println("Kode OTP: " + otp);
        System.out.println("Berlaku selama 5 menit.");
        System.out.println("================================");

        return otp;
    }

    public boolean verifyOtp(String email, String otp) {
        String key = email.toLowerCase();
        OtpData data = otpStorage.get(key);

        if (data == null) {
            return false;
        }

        if (LocalDateTime.now().isAfter(data.expiry)) {
            otpStorage.remove(key);
            return false;
        }

        if (!data.otp.equals(otp)) {
            return false;
        }

        otpStorage.remove(key);
        return true;
    }

    private static class OtpData {
        private final String otp;
        private final LocalDateTime expiry;

        private OtpData(String otp, LocalDateTime expiry) {
            this.otp = otp;
            this.expiry = expiry;
        }
    }
}
