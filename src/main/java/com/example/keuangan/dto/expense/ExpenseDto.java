package com.example.keuangan.dto.expense;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class ExpenseDto {
    private Long id;
    private Long userId;
    private String category;
    private BigDecimal nominal;
    private LocalDate tanggal;
    private String keterangan;
}
