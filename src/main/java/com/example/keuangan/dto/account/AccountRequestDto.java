package com.example.keuangan.dto.account;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountRequestDto {
    private String code;
    private String name;
    private String type;
}
