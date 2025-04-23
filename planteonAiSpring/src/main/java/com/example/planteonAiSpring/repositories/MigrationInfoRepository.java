package com.example.planteonAiSpring.repositories;

import com.example.planteonAiSpring.entities.MigrationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MigrationInfoRepository extends JpaRepository<MigrationInfo, Long> {
    boolean existsByMigrationName(String migrationName);
}
