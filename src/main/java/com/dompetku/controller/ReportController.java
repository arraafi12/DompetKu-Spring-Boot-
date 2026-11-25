package com.dompetku.controller;

import com.dompetku.entity.Transaction;
import com.dompetku.service.TransactionService;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReportController {

    private final TransactionService transactionService;

    public ReportController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Menampilkan laporan transaksi pada halaman web
    @GetMapping("/reports")
    public String showReport(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            Model model) {

        LocalDate today = LocalDate.now();

        if (startDate == null) {
            startDate = today.withDayOfMonth(1);
        }

        if (endDate == null) {
            endDate = today;
        }

        List<Transaction> transactions =
                transactionService.getTransactionsByPeriod(startDate, endDate);

        BigDecimal totalIncome = transactions.stream()
                .filter(t -> "PEMASUKAN".equals(t.getType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactions.stream()
                .filter(t -> "PENGELUARAN".equals(t.getType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = totalIncome.subtract(totalExpense);

        model.addAttribute("transactions", transactions);
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpense", totalExpense);
        model.addAttribute("balance", balance);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "reports";
    }

    // Menghasilkan laporan transaksi dalam bentuk PDF
    @GetMapping("/reports/pdf")
    public ResponseEntity<byte[]> generateReportPdf(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        try {
            LocalDate today = LocalDate.now();

            if (startDate == null) {
                startDate = today.withDayOfMonth(1);
            }

            if (endDate == null) {
                endDate = today;
            }

            // Mengambil transaksi semua pengguna berdasarkan periode
            List<Transaction> transactions =
                    transactionService.getTransactionsByPeriod(startDate, endDate);

            // Menghitung total pemasukan
            BigDecimal totalIncome = transactions.stream()
                    .filter(t -> "PEMASUKAN".equals(t.getType()))
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Menghitung total pengeluaran
            BigDecimal totalExpense = transactions.stream()
                    .filter(t -> "PENGELUARAN".equals(t.getType()))
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Menghitung saldo akhir
            BigDecimal balance = totalIncome.subtract(totalExpense);

            // Format mata uang Rupiah
            NumberFormat currencyFormat =
                    NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

            DateTimeFormatter dateFormatter =
                    DateTimeFormatter.ofPattern("dd-MM-yyyy");

            // Parameter untuk template JasperReports
            Map<String, Object> parameters = new HashMap<>();

            parameters.put(
                    "periodeLaporan",
                    startDate.format(dateFormatter)
                    + " s/d "
                    + endDate.format(dateFormatter)
            );

            parameters.put("totalIncomeText",
                    currencyFormat.format(totalIncome));

            parameters.put("totalExpenseText",
                    currencyFormat.format(totalExpense));

            parameters.put("balanceText",
                    currencyFormat.format(balance));

            // Mengirim data transaksi ke JasperReports
            JRBeanCollectionDataSource dataSource =
                    new JRBeanCollectionDataSource(transactions);

            // Mengambil file JRXML dari folder resources/reports
            ClassPathResource resource = new ClassPathResource(
                    "reports/LaporanTransaksiDompetKu_Dinamis1.jrxml"
            );

            if (!resource.exists()) {
                throw new IllegalStateException(
                        "File JRXML tidak ditemukan. Pastikan file berada di "
                        + "src/main/resources/reports/ dan bernama "
                        + "LaporanTransaksiDompetKu_Dinamis1.jrxml"
                );
            }

            JasperPrint jasperPrint;

            try (InputStream inputStream = resource.getInputStream()) {

                var jasperReport =
                        JasperCompileManager.compileReport(inputStream);

                jasperPrint = JasperFillManager.fillReport(
                        jasperReport,
                        parameters,
                        dataSource
                );
            }

            // Mengubah laporan menjadi PDF
            byte[] pdf =
                    JasperExportManager.exportReportToPdf(jasperPrint);

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=LaporanTransaksiDompetKu.pdf"
                    )
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);

        } catch (Exception e) {

            // Menampilkan penyebab error di Output NetBeans
            e.printStackTrace();

            // Pesan sementara untuk pemeriksaan error
            String errorMessage = e.getClass().getName()
                    + ": " + e.getMessage();

            return ResponseEntity.internalServerError()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(errorMessage.getBytes(
                            java.nio.charset.StandardCharsets.UTF_8
                    ));
        }
    }
}