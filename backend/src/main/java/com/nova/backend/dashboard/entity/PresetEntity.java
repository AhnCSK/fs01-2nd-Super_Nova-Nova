package com.nova.backend.dashboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "preset")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PresetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "preset_id")
    private Long presetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")   // 추천 프리셋은 NULL
    private UserEntity user;

    @Column(name = "plant_type", nullable = false)
    private String plantType;

    @Column(name = "preset_name")
    private String presetName;
}
