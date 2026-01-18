package com.example.keuangan.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.keuangan.dto.family.FamilyDto;
import com.example.keuangan.entity.Family;
import com.example.keuangan.entity.User;
import com.example.keuangan.repository.FamilyRepository;
import com.example.keuangan.repository.UserRepository;

@Service
public class FamilyService {

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public FamilyDto createFamily(String email, String familyName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getFamily() != null) {
            throw new IllegalArgumentException("User already belongs to a family");
        }

        Family family = new Family();
        family.setName(familyName);
        family.setCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        family = familyRepository.save(family);

        user.setFamily(family);
        userRepository.save(user);

        return mapToDto(family);
    }

    @Transactional
    public FamilyDto joinFamily(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getFamily() != null) {
            throw new IllegalArgumentException("User already belongs to a family");
        }

        Family family = familyRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid family code"));

        user.setFamily(family);
        userRepository.save(user);

        return mapToDto(family);
    }

    private FamilyDto mapToDto(Family family) {
        FamilyDto dto = new FamilyDto();
        dto.setId(family.getId());
        dto.setName(family.getName());
        dto.setCode(family.getCode());
        return dto;
    }
}
