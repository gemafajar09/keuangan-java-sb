package com.example.keuangan.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.keuangan.entity.Family;

@Repository
public interface FamilyRepository extends JpaRepository<Family, Long> {
    Optional<Family> findByCode(String code);
}
