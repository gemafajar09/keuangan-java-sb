package com.example.keuangan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.keuangan.entity.FinancialTransaction;

public interface TransactionRepository extends JpaRepository<FinancialTransaction, Long> {
    @Query("""
        SELECT DISTINCT t
        FROM FinancialTransaction t
        LEFT JOIN FETCH t.details d
        LEFT JOIN FETCH d.account
    """)
    List<FinancialTransaction> findAllWithDetails();
}
