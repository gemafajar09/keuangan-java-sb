package com.example.keuangan.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.keuangan.dto.account.AccountResponseDto;
import com.example.keuangan.payload.ApiResponse;
import com.example.keuangan.dto.transaction.FinancialTransactionResponseDto;
import com.example.keuangan.dto.transaction.TransactionDetailResponseDto;
import com.example.keuangan.dto.transaction.TransactionRequestDto;
import com.example.keuangan.dto.transaction.TransactionResponseDto;
import com.example.keuangan.repository.TransactionRepository;
import com.example.keuangan.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/transactions")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@Tag(name = "Transaction Controller", description = "Manage financial transactions")
public class TransactionController {
    private TransactionService transactionService;
    private TransactionRepository transactionRepository;

    public TransactionController(TransactionService transactionService, TransactionRepository transactionRepository) {
        this.transactionService = transactionService;
        this.transactionRepository = transactionRepository;
    }

    @PostMapping
    @Operation(summary = "Create transaction", description = "Record a new financial transaction with details")
    public ResponseEntity<ApiResponse<TransactionResponseDto>> create(@RequestBody TransactionRequestDto request) {
        try {
            TransactionResponseDto result = transactionService.createTransaction(request);

            return ResponseEntity.ok(
                    ApiResponse.success("Transaction created", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Get all transactions", description = "Retrieve list of all transactions with details")
    public ResponseEntity<ApiResponse<List<FinancialTransactionResponseDto>>> getAll() {

        List<FinancialTransactionResponseDto> response = transactionRepository.findAllWithDetails()
                .stream()
                .map(t -> {
                    FinancialTransactionResponseDto dto = new FinancialTransactionResponseDto();
                    dto.setId(t.getId());
                    dto.setTransactionDate(t.getTransactionDate());
                    dto.setReference(t.getReference());
                    dto.setDescription(t.getDescription());

                    if (t.getDetails() != null) {
                        dto.setDetails(
                                t.getDetails().stream()
                                        .map(d -> {
                                            TransactionDetailResponseDto dr = new TransactionDetailResponseDto();
                                            dr.setId(d.getId());
                                            dr.setDebit(d.getDebit());
                                            dr.setCredit(d.getCredit());
                                            dr.setAccountId(d.getAccount().getId());
                                            dr.setAccounts(List.of(
                                                    new AccountResponseDto(
                                                            d.getAccount().getId(),
                                                            d.getAccount().getCode(),
                                                            d.getAccount().getName(),
                                                            d.getAccount().getType(),
                                                            d.getAccount().getBalance())));
                                            return dr;
                                        })
                                        .toList());
                    }
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(
                ApiResponse.success("Transactions retrieved", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete transaction", description = "Remove a transaction by ID")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            transactionRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage()));
        }
    }

}
