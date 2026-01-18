package com.example.keuangan.dto.user;

import com.example.keuangan.dto.family.FamilyDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private Long id;
    private String email;
    private String name;
    private String role;
    private FamilyDto family;
}
