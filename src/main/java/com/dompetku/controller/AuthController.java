package com.dompetku.controller;

import com.dompetku.entity.User;
import com.dompetku.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Menampilkan halaman login
    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    // Memproses login
    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpServletRequest request,
            Model model) {

        try {
            User user = authService.login(email, password);

            // Membuat session setelah login berhasil
            HttpSession session = request.getSession(true);

            // Mengganti ID session untuk keamanan
            request.changeSessionId();

            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getName());
            session.setAttribute("userEmail", user.getEmail());

            // Session berakhir setelah 30 menit tidak aktif
            session.setMaxInactiveInterval(30 * 60);

            return "redirect:/";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    // Menampilkan halaman registrasi
    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // Memproses registrasi
    @PostMapping("/register")
    public String register(
            @ModelAttribute User user,
            @RequestParam String confirmPassword,
            Model model) {

        if (user.getPassword() == null
                || !user.getPassword().equals(confirmPassword)) {

            model.addAttribute(
                    "error",
                    "Konfirmasi password tidak cocok."
            );

            model.addAttribute("user", user);
            return "register";
        }

        try {
            authService.register(user);

            return "redirect:/login?registered";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user);

            return "register";
        }
    }

    // Memproses logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {

        if (session != null) {
            session.invalidate();
        }

        return "redirect:/login?logout";
    }
}