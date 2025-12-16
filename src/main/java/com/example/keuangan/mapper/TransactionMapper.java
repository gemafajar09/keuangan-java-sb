package com.example.keuangan.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.keuangan.dto.TransactionDetailResponse;
import com.example.keuangan.dto.TransactionResponse;
import com.example.keuangan.entity.FinancialTransaction;
import com.example.keuangan.entity.TransactionDetail;

@Component
public class TransactionMapper {
    public TransactionResponse toResponse(FinancialTransaction trx) {

        List<TransactionDetailResponse> detailResponses =
                trx.getDetails().stream()
                        .map(this::mapDetail)
                        .toList();

        BigDecimal totalDebit = detailResponses.stream()
                .map(TransactionDetailResponse::getDebit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredit = detailResponses.stream()
                .map(TransactionDetailResponse::getCredit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new TransactionResponse(
                trx.getId(),
                trx.getTransactionDate(),
                trx.getReference(),
                trx.getDescription(),
                totalDebit,
                totalCredit,
                detailResponses
        );
    }

    private TransactionDetailResponse mapDetail(TransactionDetail detail) {
        return new TransactionDetailResponse(
                detail.getId(),
                detail.getAccount().getId(),
                detail.getDebit(),
                detail.getCredit(),
                null
        );
    }
}
