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

import com.example.keuangan.dto.AccountResponse;
import com.example.keuangan.dto.ApiResponse;
import com.example.keuangan.dto.FinancialTransactionResponse;
import com.example.keuangan.dto.TransactionDetailResponse;
import com.example.keuangan.dto.TransactionRequest;
import com.example.keuangan.dto.TransactionResponse;
import com.example.keuangan.repository.TransactionRepository;
import com.example.keuangan.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class TransactionController {
    private TransactionService transactionService;
    private TransactionRepository transactionRepository;
    
    @Autowired
    public TransactionController(TransactionService transactionService, TransactionRepository transactionRepository) {
        this.transactionService = transactionService;
        this.transactionRepository = transactionRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponse>> create(@RequestBody TransactionRequest request) {
        try {
            TransactionResponse result = transactionService.createTransaction(request);
            
            return ResponseEntity.ok(
                    ApiResponse.success("Transaction created", result)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FinancialTransactionResponse>>> getAll() {

        List<FinancialTransactionResponse> response =
            transactionRepository.findAllWithDetails()
                .stream()
                .map(t -> {
                    FinancialTransactionResponse dto = new FinancialTransactionResponse();
                    dto.setId(t.getId());
                    dto.setTransactionDate(t.getTransactionDate());
                    dto.setReference(t.getReference());
                    dto.setDescription(t.getDescription());

                    if (t.getDetails() != null) {
                        dto.setDetails(
                            t.getDetails().stream()
                                .map(d -> {
                                    TransactionDetailResponse dr =
                                            new TransactionDetailResponse();
                                    dr.setId(d.getId());
                                    dr.setDebit(d.getDebit());
                                    dr.setCredit(d.getCredit());
                                    dr.setAccountId(d.getAccount().getId());
                                    dr.setAccounts(List.of(
                                        new AccountResponse(
                                            d.getAccount().getId(),
                                            d.getAccount().getName(),
                                            d.getAccount().getType(), null
                                        )
                                    ));
                                    return dr;
                                })
                                .toList()
                        );
                    }
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(
            ApiResponse.success("Transactions retrieved", response)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            transactionRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );
        }
    }

}
