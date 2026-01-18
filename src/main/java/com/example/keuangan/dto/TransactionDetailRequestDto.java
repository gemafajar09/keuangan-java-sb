package com.example.keuangan.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class TransactionDetailRequestDto {

    private Long accountId;
    private BigDecimal debit;
    private BigDecimal credit;
}
