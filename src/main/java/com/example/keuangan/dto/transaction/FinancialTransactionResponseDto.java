package com.example.keuangan.dto.transaction;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class FinancialTransactionResponseDto {
    private Long id;
    private LocalDate transactionDate;
    private String reference;
    private String description;
    private List<TransactionDetailResponseDto> details;
}
