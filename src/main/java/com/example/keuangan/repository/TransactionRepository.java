package com.example.keuangan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.keuangan.entity.FinancialTransaction;

public interface TransactionRepository extends JpaRepository<FinancialTransaction, Long> {}
