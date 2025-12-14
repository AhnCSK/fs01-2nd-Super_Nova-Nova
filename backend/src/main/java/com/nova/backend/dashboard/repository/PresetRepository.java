package com.nova.backend.dashboard.repository;

import com.nova.backend.dashboard.entity.PresetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// 프리셋 정보조회
@Repository
public interface PresetRepository extends JpaRepository<PresetEntity, Long> {
}
