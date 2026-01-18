package com.example.keuangan.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.keuangan.dto.family.FamilyDto;
import com.example.keuangan.payload.ApiResponse;
import com.example.keuangan.service.FamilyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/families")
@Tag(name = "Family Controller", description = "Manage family and household")
public class FamilyController {

    @Autowired
    private FamilyService familyService;

    @PostMapping
    @Operation(summary = "Create family", description = "Create a new family group")
    public ResponseEntity<ApiResponse<FamilyDto>> createFamily(@RequestBody Map<String, String> request,
            Principal principal) {
        try {
            String name = request.get("name");
            FamilyDto family = familyService.createFamily(principal.getName(), name);
            return ResponseEntity.ok(ApiResponse.success("Family created successfully", family));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/join")
    @Operation(summary = "Join family", description = "Join an existing family using code")
    public ResponseEntity<ApiResponse<FamilyDto>> joinFamily(@RequestBody Map<String, String> request,
            Principal principal) {
        try {
            String code = request.get("code");
            FamilyDto family = familyService.joinFamily(principal.getName(), code);
            return ResponseEntity.ok(ApiResponse.success("Joined family successfully", family));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
