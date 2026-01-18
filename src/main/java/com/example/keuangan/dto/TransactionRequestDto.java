package com.example.keuangan.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class TransactionRequestDto {
    private LocalDate transactionDate;
    private String reference;
    private String description;
    private List<TransactionDetailRequestDto> details;
}
