package com.dompetku.controller;

import com.dompetku.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final TransactionService transactionService;

    public HomeController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/")
    public String home(Model model, HttpSession session) {

        model.addAttribute("totalIncome",
                transactionService.getTotalIncome());

        model.addAttribute("totalExpense",
                transactionService.getTotalExpense());

        model.addAttribute("balance",
                transactionService.getBalance());

        model.addAttribute("transactions",
                transactionService.getAllTransactions());

        model.addAttribute("userName",
                session.getAttribute("userName"));

        return "index";
    }
}