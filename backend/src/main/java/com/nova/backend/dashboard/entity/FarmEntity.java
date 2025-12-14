package com.nova.backend.dashboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "farm")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "farm_id")
    private Long farmId;

    @Column(name = "farm_name")
    private String farmName;

    @Column(nullable = false)
    private int slot;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nova_id", nullable = false)
    private NovaEntity nova;

    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "step_id", nullable = false)
    private PresetStepEntity presetStep;
}
