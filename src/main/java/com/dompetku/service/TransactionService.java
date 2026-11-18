package com.dompetku.service;

import com.dompetku.entity.Transaction;
import com.dompetku.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // Mengambil semua transaksi
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // Mengambil transaksi berdasarkan ID
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    // Menyimpan transaksi baru atau perubahan transaksi
    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    // Menghapus transaksi
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    // Menghitung seluruh pemasukan
    public BigDecimal getTotalIncome() {
        return transactionRepository.findAll()
                .stream()
                .filter(t -> "PEMASUKAN".equals(t.getType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Menghitung seluruh pengeluaran
    public BigDecimal getTotalExpense() {
        return transactionRepository.findAll()
                .stream()
                .filter(t -> "PENGELUARAN".equals(t.getType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Menghitung total saldo
    public BigDecimal getBalance() {
        return getTotalIncome().subtract(getTotalExpense());
    }

    // Mencari dan memfilter transaksi
    public List<Transaction> searchAndFilter(
            String keyword,
            String type,
            String category,
            LocalDate startDate,
            LocalDate endDate) {

        return transactionRepository.searchAndFilter(
                keyword,
                type,
                category,
                startDate,
                endDate
        );
    }

    // Mengambil transaksi berdasarkan periode
    public List<Transaction> getTransactionsByPeriod(
            LocalDate startDate,
            LocalDate endDate) {

        return transactionRepository.findByTransactionDateBetween(
                startDate,
                endDate
        );
    }
}
