package com.example.keuangan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.lang.NonNull;

import com.example.keuangan.dto.account.AccountRequestDto;
import com.example.keuangan.dto.account.AccountResponseDto;
import com.example.keuangan.payload.ApiResponse;
import com.example.keuangan.service.AccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@Tag(name = "Account Controller", description = "Manage financial accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    @Operation(summary = "Get all accounts", description = "Retrieve list of all registered accounts")
    public ApiResponse<List<AccountResponseDto>> getAllAccounts() {
        List<AccountResponseDto> accounts = accountService.getAllAccounts();
        return new ApiResponse<>(true, "Accounts retrieved successfully", accounts);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account by ID", description = "Retrieve a specific account details")
    public ApiResponse<AccountResponseDto> getAccountById(@PathVariable @NonNull Long id) {
        AccountResponseDto account = accountService.getAccountById(id);
        return new ApiResponse<>(true, "Account retrieved successfully", account);
    }

    @PostMapping
    @Operation(summary = "Create new account", description = "Register a new financial account")
    public ApiResponse<AccountResponseDto> createAccount(@RequestBody AccountRequestDto request) {
        AccountResponseDto savedAccount = accountService.createAccount(request);
        return new ApiResponse<>(true, "Account created successfully", savedAccount);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update account", description = "Update an existing account")
    public ApiResponse<AccountResponseDto> updateAccount(@PathVariable @NonNull Long id,
            @RequestBody AccountRequestDto request) {
        AccountResponseDto updatedAccount = accountService.updateAccount(id, request);
        return new ApiResponse<>(true, "Account updated successfully", updatedAccount);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete account", description = "Remove an account by ID")
    public ResponseEntity<?> deleteAccount(@PathVariable Long id) {
        try {
            return ResponseEntity.ok("Account deleted (method placeholder)");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting account");
        }
    }
}
