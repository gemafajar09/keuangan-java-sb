package com.example.keuangan.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.keuangan.dto.installment.InstallmentDto;
import com.example.keuangan.payload.ApiResponse;
import com.example.keuangan.service.FinanceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/installments")
@Tag(name = "Installment Controller", description = "Manage installments and loans")
public class InstallmentController {

    @Autowired
    private FinanceService financeService;

    @PostMapping
    @Operation(summary = "Add installment", description = "Record new installment plan")
    public ResponseEntity<ApiResponse<InstallmentDto>> addInstallment(@RequestBody InstallmentDto request,
            Principal principal) {
        InstallmentDto result = financeService.addInstallment(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Installment added successfully", result));
    }

    @GetMapping
    @Operation(summary = "Get installments", description = "Get list of installments")
    public ResponseEntity<ApiResponse<List<InstallmentDto>>> getInstallments(Principal principal) {
        List<InstallmentDto> result = financeService.getInstallments(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Installments retrieved successfully", result));
    }

    @PostMapping("/{id}/pay")
    @Operation(summary = "Pay installment", description = "Record payment for an installment")
    public ResponseEntity<ApiResponse<String>> payInstallment(@PathVariable Long id,
            @RequestBody Map<String, BigDecimal> request) {
        BigDecimal amount = request.get("amount");
        if (amount == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Amount is required"));
        }
        financeService.payInstallment(id, amount);
        return ResponseEntity.ok(ApiResponse.success("Payment recorded successfully", null));
    }
}
