package com.example.keuangan.dto;

import javax.management.relation.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    private Role role;
}
