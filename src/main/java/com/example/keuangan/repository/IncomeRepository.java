package com.example.keuangan.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.keuangan.entity.Income;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findByUserId(Long userId);

    @Query("SELECT i FROM Income i WHERE i.user.family.id = :familyId")
    List<Income> findByFamilyId(@Param("familyId") Long familyId);

    List<Income> findByUserIdAndTanggalBetween(Long userId, LocalDate startDate, LocalDate endDate);
}
