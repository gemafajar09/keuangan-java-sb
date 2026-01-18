package com.example.keuangan.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.keuangan.dto.report.SummaryResponseDto;
import com.example.keuangan.payload.ApiResponse;
import com.example.keuangan.service.FinanceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Report Controller", description = "Financial reports")
public class ReportController {

    @Autowired
    private FinanceService financeService;

    @GetMapping("/monthly")
    @Operation(summary = "Get monthly summary", description = "Get financial summary for a specific month")
    public ResponseEntity<ApiResponse<SummaryResponseDto>> getMonthlySummary(
            @RequestParam int month,
            @RequestParam int year,
            Principal principal) {

        SummaryResponseDto result = financeService.getMonthlySummary(principal.getName(), month, year);
        return ResponseEntity.ok(ApiResponse.success("Summary retrieved successfully", result));
    }
}
