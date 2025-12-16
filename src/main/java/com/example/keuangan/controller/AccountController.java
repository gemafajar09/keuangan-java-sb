package com.example.keuangan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.keuangan.dto.AccountRequest;
import com.example.keuangan.dto.ApiResponse;
import com.example.keuangan.service.AccountService;

@RestController
@RequestMapping("/api/accounts")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class AccountController {
    private AccountService accountService;
    
    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    } 

    @GetMapping
    public ResponseEntity<?> getAllAccounts() {
        try {
            var accounts = accountService.findAll();
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error("Could not retrieve accounts.")
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccountById(Long id) {
        try {
            var account = accountService.findById(id);
            if (account.isPresent()) {
                return ResponseEntity.ok(account.get());
            } else {
                return ResponseEntity.status(404).body("Account not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error("Invalid account ID.")
            );
        }
    }

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody AccountRequest request) {
        try {
            var savedAccount = accountService.createAccount(request);
            return ResponseEntity.ok(
                ApiResponse.success("Account created", savedAccount)
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while creating the account.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(Long id) {
        try {
            accountService.deleteAccount(id);
            return ResponseEntity.ok("Account deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Account not found.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while deleting the account.");
        }
    }
}
