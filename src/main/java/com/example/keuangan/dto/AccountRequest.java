package com.example.keuangan.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountRequest {
    private String code;
    private String name;
    private String type;
}
