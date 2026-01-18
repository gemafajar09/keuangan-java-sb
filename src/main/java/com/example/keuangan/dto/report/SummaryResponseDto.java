package com.example.keuangan.dto.report;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummaryResponseDto {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal totalInstallment;
    private BigDecimal finalBalance;
    private String period;
}
