package com.example.keuangan.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.keuangan.dto.TransactionDetailResponseDto;
import com.example.keuangan.dto.TransactionResponseDto;
import com.example.keuangan.entity.FinancialTransaction;
import com.example.keuangan.entity.TransactionDetail;

@Component
public class TransactionMapper {
    public TransactionResponseDto toResponse(FinancialTransaction trx) {

        List<TransactionDetailResponseDto> detailResponses =
                trx.getDetails().stream()
                        .map(this::mapDetail)
                        .toList();

        BigDecimal totalDebit = detailResponses.stream()
                .map(TransactionDetailResponseDto::getDebit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredit = detailResponses.stream()
                .map(TransactionDetailResponseDto::getCredit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new TransactionResponseDto(
                trx.getId(),
                trx.getTransactionDate(),
                trx.getReference(),
                trx.getDescription(),
                totalDebit,
                totalCredit,
                detailResponses
        );
    }

    private TransactionDetailResponseDto mapDetail(TransactionDetail detail) {
        return new TransactionDetailResponseDto(
                detail.getId(),
                detail.getAccount().getId(),
                detail.getDebit(),
                detail.getCredit(),
                null // Description is null in previous code
        );
    }
}
