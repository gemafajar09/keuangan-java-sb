package com.example.keuangan.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDetailResponseDto {
    private Long id;
    private Long accountId;
    private BigDecimal debit;
    private BigDecimal credit;
    private List<AccountResponseDto> accounts;
}
