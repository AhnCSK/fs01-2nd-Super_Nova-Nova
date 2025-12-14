package com.nova.backend.dashboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prset_step")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PresetStepEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "step_id")
    private Long stepId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "preset_id")
    private PresetStepEntity preset;

    @Column(name = "growth_step", nullable = false)
    private int growthStep;

    @Column(name = "period_days", nullable = false)
    private int periodDays;

    @Column(name = "temp", columnDefinition = "json", nullable = false)
    private String tempJson;

    @Column(name = "humidity", columnDefinition = "json", nullable = false)
    private String humiJson;

    @Column(name = "lightPower", columnDefinition = "json", nullable = false)
    private String lightJson;

    @Column(name = "co2", columnDefinition = "json", nullable = false)
    private String co2Json;

    @Column(name = "soil_moisture", columnDefinition = "json", nullable = false)
    private String soilJson;
}
