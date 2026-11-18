package com.dompetku.controller;

import com.dompetku.entity.Transaction;
import com.dompetku.service.TransactionService;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Menampilkan transaksi, pencarian, dan filter
    @GetMapping("/transactions")
    public String getAllTransactions(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,
            Model model) {

        List<Transaction> transactions =
                transactionService.searchAndFilter(
                        keyword, type, category, startDate, endDate
                );

        List<String> categories = transactionService.getAllTransactions()
                .stream()
                .map(Transaction::getCategory)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        model.addAttribute("transactions", transactions);
        model.addAttribute("categories", categories);

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "transactions";
    }

    // Menampilkan halaman tambah transaksi
    @GetMapping("/transactions/new")
    public String showForm(Model model) {
        model.addAttribute("transaction", new Transaction());
        return "transaction-form";
    }

    // Menampilkan form edit transaksi
    @GetMapping("/transactions/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        return transactionService.getTransactionById(id)
                .map(transaction -> {
                    model.addAttribute("transaction", transaction);
                    return "transaction-form";
                })
                .orElse("redirect:/transactions");
    }

    // Menyimpan transaksi
    @PostMapping("/transactions/save")
    public String saveTransaction(
            @ModelAttribute Transaction transaction) {

        transactionService.saveTransaction(transaction);
        return "redirect:/transactions";
    }

    // Menghapus transaksi
    @GetMapping("/transactions/delete/{id}")
    public String deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return "redirect:/transactions";
    }
}