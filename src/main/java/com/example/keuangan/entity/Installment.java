package com.example.keuangan.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "installments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Installment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "nama_cicilan", nullable = false)
    private String namaCicilan;

    @Column(name = "total_cicilan", nullable = false)
    private BigDecimal totalCicilan;

    @Column(name = "cicilan_bulanan", nullable = false)
    private BigDecimal cicilanBulanan;

    @Column(nullable = false)
    private Integer tenor;

    @Column(name = "sisa_cicilan", nullable = false)
    private BigDecimal sisaCicilan;

    @Column(nullable = false)
    private String status;
}
