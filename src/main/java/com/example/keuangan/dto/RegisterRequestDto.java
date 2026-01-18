package com.example.keuangan.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDto {

    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    private Long roleId;
}
