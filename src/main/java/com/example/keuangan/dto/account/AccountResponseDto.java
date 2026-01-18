package com.example.keuangan.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponseDto {

    private Long id;
    private String code;
    private String name;
    private String type;
    private java.math.BigDecimal balance;
}
