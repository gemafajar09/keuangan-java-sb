package com.example.keuangan.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseDto {
    private Long id;
    private LocalDate transactionDate;
    private String reference;
    private String description;
    private BigDecimal totalDebit;
    private BigDecimal totalCredit;
    private List<TransactionDetailResponseDto> details;
}
