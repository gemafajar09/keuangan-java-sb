package com.example.keuangan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.keuangan.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {}
