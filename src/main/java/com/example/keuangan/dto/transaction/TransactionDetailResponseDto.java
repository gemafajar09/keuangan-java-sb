package com.example.keuangan.dto.transaction;

import java.math.BigDecimal;
import java.util.List;

import com.example.keuangan.dto.account.AccountResponseDto;
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
