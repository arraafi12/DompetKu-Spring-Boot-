package com.dompetku.controller;

import com.dompetku.service.AuthService;
import com.dompetku.service.OtpService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthRecoveryController {

    private final AuthService authService;
    private final OtpService otpService;

    public AuthRecoveryController(AuthService authService,
                                  OtpService otpService) {
        this.authService = authService;
        this.otpService = otpService;
    }

    // HALAMAN LUPA EMAIL
    @GetMapping("/forgot-email")
    public String showForgotEmail() {
        return "forgot-email";
    }

    @PostMapping("/forgot-email")
    public String recoverEmail(
            @RequestParam String name,
            @RequestParam String phone,
            Model model) {

        try {
            String recoveredEmail =
                    authService.recoverEmail(name, phone);

            model.addAttribute("recoveredEmail", recoveredEmail);
            model.addAttribute("success",
                    "Data akun berhasil ditemukan.");

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }

        return "forgot-email";
    }

    // HALAMAN LUPA PASSWORD
    @GetMapping("/forgot-password")
    public String showForgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String sendOtp(
            @RequestParam String email,
            RedirectAttributes redirectAttributes) {

        if (!authService.emailExists(email)) {
            redirectAttributes.addFlashAttribute(
                    "error", "Email tidak ditemukan.");
            return "redirect:/forgot-password";
        }

        otpService.generateOtp(email.trim());

        redirectAttributes.addFlashAttribute(
                "success",
                "Kode OTP berhasil dibuat. Periksa CMD untuk melihat kode.");

        redirectAttributes.addAttribute("email", email.trim());

        return "redirect:/reset-password";
    }

    // HALAMAN RESET PASSWORD
    @GetMapping("/reset-password")
    public String showResetPassword(
            @RequestParam(required = false) String email,
            Model model) {

        model.addAttribute("email", email);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Model model) {

        if (newPassword.length() < 8) {
            model.addAttribute("error",
                    "Password minimal 8 karakter.");
            model.addAttribute("email", email);
            return "reset-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error",
                    "Konfirmasi password tidak cocok.");
            model.addAttribute("email", email);
            return "reset-password";
        }

        if (!otpService.verifyOtp(email.trim(), otp.trim())) {
            model.addAttribute("error",
                    "Kode OTP salah atau sudah kedaluwarsa.");
            model.addAttribute("email", email);
            return "reset-password";
        }

        try {
            authService.resetPassword(email, newPassword);
            return "redirect:/login?resetSuccess";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
            return "reset-password";
        }
    }
}