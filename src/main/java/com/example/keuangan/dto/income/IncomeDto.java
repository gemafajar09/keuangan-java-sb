package com.example.keuangan.dto.income;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class IncomeDto {
    private Long id;
    private Long userId;
    private String sumberPemasukan;
    private BigDecimal nominal;
    private LocalDate tanggal;
    private String keterangan;
}
