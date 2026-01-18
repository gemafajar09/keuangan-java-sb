package com.example.keuangan.seeder;

import com.example.keuangan.entity.Role;
import com.example.keuangan.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final RoleRepository roleRepository;

    @Bean
    public CommandLineRunner initRoles() {
        return args -> {
            try {
                if (roleRepository.count() == 0) {
                    Role userRole = new Role();
                    userRole.setName("USER");
                    roleRepository.save(userRole);

                    Role adminRole = new Role();
                    adminRole.setName("ADMIN");
                    roleRepository.save(adminRole);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}
