package com.example.keuangan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.keuangan.entity.Installment;

@Repository
public interface InstallmentRepository extends JpaRepository<Installment, Long> {
    List<Installment> findByUserId(Long userId);

    @Query("SELECT i FROM Installment i WHERE i.user.family.id = :familyId")
    List<Installment> findByFamilyId(@Param("familyId") Long familyId);

    List<Installment> findByStatus(String status);
}
