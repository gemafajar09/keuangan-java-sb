package com.example.keuangan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.keuangan.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
