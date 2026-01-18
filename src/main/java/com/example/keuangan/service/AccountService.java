package com.example.keuangan.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.lang.NonNull;

import com.example.keuangan.dto.account.AccountRequestDto;
import com.example.keuangan.dto.account.AccountResponseDto;
import com.example.keuangan.entity.Account;
import com.example.keuangan.repository.AccountRepository;
import com.example.keuangan.util.BaseServiceUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService extends BaseServiceUtil {

    private AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<AccountResponseDto> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AccountResponseDto getAccountById(@NonNull Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return mapToResponse(account);
    }

    public AccountResponseDto createAccount(AccountRequestDto accountRequest) {
        Account account = new Account();
        account.setCode(accountRequest.getCode());
        account.setName(accountRequest.getName());
        account.setType(accountRequest.getType());
        Account savedAccount = accountRepository.save(account);
        return mapToResponse(savedAccount);
    }

    public AccountResponseDto updateAccount(@NonNull Long id, AccountRequestDto accountRequest) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setCode(accountRequest.getCode());
        account.setName(accountRequest.getName());
        account.setType(accountRequest.getType());
        Account updatedAccount = accountRepository.save(account);
        return mapToResponse(updatedAccount);
    }

    private AccountResponseDto mapToResponse(Account account) {
        return new AccountResponseDto(
                account.getId(),
                account.getCode(),
                account.getName(),
                account.getType(),
                account.getBalance());
    }

    @Transactional
    public void hapusAccount(@NonNull Long id) {
        try {
            accountRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Account not found"));
            accountRepository.deleteById(id);
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }
}
