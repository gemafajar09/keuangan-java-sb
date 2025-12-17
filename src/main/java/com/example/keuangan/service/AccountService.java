package com.example.keuangan.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.keuangan.dto.AccountRequest;
import com.example.keuangan.dto.AccountResponse;
import com.example.keuangan.entity.Account;
import com.example.keuangan.repository.AccountRepository;
import com.example.keuangan.util.BaseService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService extends BaseService {
    
    private AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> cariSemua() {
        return accountRepository.findAll();
    }

    public Optional<Account> cariById(Long id) {
        return accountRepository.findById(id);
    }

    public AccountResponse buatAccount(AccountRequest request) {
        Account account = new Account();
        account.setCode(request.getCode());
        account.setName(request.getName());
        account.setType(request.getType());
        Account savedAccount = accountRepository.save(account);
        return new AccountResponse(
            savedAccount.getId(),
            savedAccount.getCode(),
            savedAccount.getName(),
            savedAccount.getType()
        );
    }

    @Transactional
    public void hapusAccount(Long id) {
        try {
            accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
            accountRepository.deleteById(id);
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }
}
