package com.example.keuangan.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.keuangan.dto.ApiResponse;
import com.example.keuangan.dto.TransactionRequest;
import com.example.keuangan.dto.TransactionResponse;
import com.example.keuangan.entity.FinancialTransaction;
import com.example.keuangan.repository.TransactionRepository;
import com.example.keuangan.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;
    private TransactionRepository transactionRepository;
    
    public TransactionController(TransactionService transactionService, TransactionRepository transactionRepository) {
        this.transactionService = transactionService;
        this.transactionRepository = transactionRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponse>> create(
            @RequestBody TransactionRequest request
    ) {
        TransactionResponse result = transactionService.createTransaction(request);

        return ResponseEntity.ok(
                ApiResponse.success("Transaction created", result)
        );
    }

    @GetMapping
    public List<FinancialTransaction> getAll() {
        return transactionRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        transactionRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

}
