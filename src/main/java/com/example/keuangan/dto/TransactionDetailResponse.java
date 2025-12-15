package com.example.keuangan.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDetailResponse {
    private Long id;
    private AccountResponse account;
    private BigDecimal debit;
    private BigDecimal credit;
}
