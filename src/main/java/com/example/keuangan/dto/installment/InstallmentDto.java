package com.example.keuangan.dto.installment;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class InstallmentDto {
    private Long id;
    private Long userId;
    private String namaCicilan;
    private BigDecimal totalCicilan;
    private BigDecimal cicilanBulanan;
    private Integer tenor;
    private BigDecimal sisaCicilan;
    private String status;
}
