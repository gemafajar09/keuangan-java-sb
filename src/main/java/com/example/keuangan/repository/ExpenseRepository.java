package com.example.keuangan.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.keuangan.entity.Expense;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserId(Long userId);

    @Query("SELECT e FROM Expense e WHERE e.user.family.id = :familyId")
    List<Expense> findByFamilyId(@Param("familyId") Long familyId);

    List<Expense> findByUserIdAndTanggalBetween(Long userId, LocalDate startDate, LocalDate endDate);
}
