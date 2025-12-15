package com.example.keuangan.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.keuangan.dto.TransactionDetailRequest;
import com.example.keuangan.dto.TransactionRequest;
import com.example.keuangan.dto.TransactionResponse;
import com.example.keuangan.entity.Account;
import com.example.keuangan.entity.FinancialTransaction;
import com.example.keuangan.entity.TransactionDetail;
import com.example.keuangan.mapper.TransactionMapper;
import com.example.keuangan.repository.AccountRepository;
import com.example.keuangan.repository.TransactionRepository;

@Service
public class TransactionService {

    private static final Logger log =
            LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    private TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            TransactionMapper transactionMapper
    ) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.transactionMapper = transactionMapper;
    }

    public TransactionResponse createTransaction(TransactionRequest request) {

        FinancialTransaction transaction = new FinancialTransaction();
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setReference(request.getReference());
        transaction.setDescription(request.getDescription());

        List<TransactionDetail> details = request.getDetails().stream()
                .map(this::mapDetail)
                .toList();

        transaction.setDetails(details);
        details.forEach(d -> d.setTransaction(transaction));

        validateDebitCredit(details);

        FinancialTransaction saved = transactionRepository.save(transaction);

        return transactionMapper.toResponse(saved);
    }

    private TransactionDetail mapDetail(TransactionDetailRequest dto) {
        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        TransactionDetail detail = new TransactionDetail();
        detail.setAccount(account);
        detail.setDebit(dto.getDebit());
        detail.setCredit(dto.getCredit());
        return detail;
    }

    private void validateDebitCredit(List<TransactionDetail> details) {
        BigDecimal totalDebit = details.stream()
                .map(d -> d.getDebit() == null ? BigDecimal.ZERO : d.getDebit())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredit = details.stream()
                .map(d -> d.getCredit() == null ? BigDecimal.ZERO : d.getCredit())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new RuntimeException("Total debit dan credit harus sama");
        }
    }

    // function lama 
    public FinancialTransaction save(FinancialTransaction transaction) {

        BigDecimal totalDebit = transaction.getDetails().stream()
                                .map(d -> d.getDebit() == null ? BigDecimal.ZERO : d.getDebit())
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredit = transaction.getDetails().stream()
                                .map(d -> d.getCredit() == null ? BigDecimal.ZERO : d.getCredit())
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if(totalDebit.compareTo(totalCredit) != 0) {
            throw new IllegalArgumentException("Total debit dan credit harus sama");
        }
        
        transaction.getDetails().forEach(d -> d.setTransaction(transaction));
        return transactionRepository.save(transaction);
    }
}
