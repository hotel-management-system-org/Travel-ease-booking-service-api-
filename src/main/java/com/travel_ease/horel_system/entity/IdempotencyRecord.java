package com.travel_ease.horel_system.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "idempotency_records",
    indexes = {
        @Index(name = "idx_idempotency_key", columnList = "idempotency_key"),
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_idempotency_key_tenant", columnNames = {"idempotency_key"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdempotencyRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "idempotency_key", nullable = false, length = 100)
    private String idempotencyKey;
    
    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @OneToOne
    @JoinColumn(name = "booking_id",unique = true)
    private Booking booking;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (expiresAt == null) {
            expiresAt = createdAt.plusHours(24);
        }
    }
}