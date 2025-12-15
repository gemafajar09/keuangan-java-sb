package com.example.keuangan.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class TransactionRequest {
    private LocalDate transactionDate;
    private String reference;
    private String description;
    private List<TransactionDetailRequest> details;
}
