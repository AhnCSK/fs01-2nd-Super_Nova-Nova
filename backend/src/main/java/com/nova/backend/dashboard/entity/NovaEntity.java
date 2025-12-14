package com.nova.backend.dashboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nova")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NovaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nova_id")
    private Long novaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",  nullable = false)
    private UserEntity user;

    @Column(name = "nova_serial_number", nullable = false , unique = true)
    private String novaSerialNumber;

    private String status;
}
