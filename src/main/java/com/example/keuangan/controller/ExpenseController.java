package com.example.keuangan.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.keuangan.dto.expense.ExpenseDto;
import com.example.keuangan.payload.ApiResponse;
import com.example.keuangan.service.FinanceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/expenses")
@Tag(name = "Expense Controller", description = "Manage expenses")
public class ExpenseController {

    @Autowired
    private FinanceService financeService;

    @PostMapping
    @Operation(summary = "Add expense", description = "Record new expense")
    public ResponseEntity<ApiResponse<ExpenseDto>> addExpense(@RequestBody ExpenseDto request, Principal principal) {
        ExpenseDto result = financeService.addExpense(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Expense added successfully", result));
    }

    @GetMapping
    @Operation(summary = "Get expenses", description = "Get list of expenses for current user")
    public ResponseEntity<ApiResponse<List<ExpenseDto>>> getExpenses(Principal principal) {
        List<ExpenseDto> result = financeService.getExpenses(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Expenses retrieved successfully", result));
    }
}
