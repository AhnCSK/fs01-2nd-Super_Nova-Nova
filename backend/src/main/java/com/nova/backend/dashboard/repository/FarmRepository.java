package com.nova.backend.dashboard.repository;

import com.nova.backend.dashboard.entity.FarmEntity;
import com.nova.backend.dashboard.entity.NovaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// slot -> farm 조회
@Repository
public interface FarmRepository extends JpaRepository<FarmEntity, Long> {
    // Nova(기기) 안에서 슬롯으로 팜 찾기
    Optional<FarmEntity> findByNovaAndSlot(NovaEntity nova, int slot);

    // slot만으로 찾고 싶다면 (기기에 슬롯이 unique라면)
    Optional<FarmEntity> findBySlot(int slot);
}
