package com.example.planteonAiSpring.migrations;

import com.example.planteonAiSpring.entities.MigrationInfo;
import com.example.planteonAiSpring.repositories.MigrationInfoRepository;
import com.example.planteonAiSpring.services.AuthService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class SetUpAdminUser {
    private final AuthService authService;
    private final MigrationInfoRepository migrationInfoRepository;

    @PostConstruct
    public void init() {
        if (!migrationInfoRepository.existsByMigrationName("SetUpAdminUser")) {
            authService.registerAdmin("admin", "admin", "admin@example.com", "123");

            MigrationInfo migrationInfo = new MigrationInfo();
            migrationInfo.setMigrationName("SetUpAdminUser");
            migrationInfo.setExecutedAt(new Date());
            migrationInfoRepository.save(migrationInfo);
        }
    }
}
