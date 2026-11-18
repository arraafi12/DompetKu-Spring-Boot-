package com.dompetku.service;

import com.dompetku.entity.User;
import com.dompetku.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email sudah terdaftar.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Email atau password salah."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException(
                    "Email atau password salah.");
        }

        return user;
    }

    public String recoverEmail(String name, String phone) {
        User user = userRepository
                .findByNameIgnoreCaseAndPhone(name.trim(), phone.trim())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Data nama dan nomor telepon tidak cocok."));

        String email = user.getEmail();
        int at = email.indexOf("@");

        if (at <= 1) {
            return email;
        }

        String firstPart = email.substring(0, 2);
        String domainPart = email.substring(at);

        return firstPart + "****" + domainPart;
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email.trim()).isPresent();
    }

    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Akun tidak ditemukan."));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
