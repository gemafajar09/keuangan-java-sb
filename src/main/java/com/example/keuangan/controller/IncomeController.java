package com.example.keuangan.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.keuangan.dto.income.IncomeDto;
import com.example.keuangan.payload.ApiResponse;
import com.example.keuangan.service.FinanceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/incomes")
@Tag(name = "Income Controller", description = "Manage incomes")
public class IncomeController {

    @Autowired
    private FinanceService financeService;

    @PostMapping
    @Operation(summary = "Add income", description = "Record new income")
    public ResponseEntity<ApiResponse<IncomeDto>> addIncome(@RequestBody IncomeDto request, Principal principal) {
        IncomeDto result = financeService.addIncome(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Income added successfully", result));
    }

    @GetMapping
    @Operation(summary = "Get incomes", description = "Get list of incomes for current user")
    public ResponseEntity<ApiResponse<List<IncomeDto>>> getIncomes(Principal principal) {
        List<IncomeDto> result = financeService.getIncomes(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Incomes retrieved successfully", result));
    }
}
