package com.travel_ease.horel_system.entity;

import com.travel_ease.horel_system.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "booking_audit",
    indexes = {
        @Index(name = "idx_audit_booking_id", columnList = "booking_id"),
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingAudit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", length = 20)
    private BookingStatus oldStatus;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 20)
    private BookingStatus newStatus;
    
    @Column(name = "changed_by", nullable = false, length = 50)
    private String changedBy;
    
    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @Column(name = "action", nullable = false, length = 50)
    private String action; // CREATED, STATUS_CHANGED, CANCELLED, etc.
    
    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id",nullable = false)
    private Booking booking;
    
    @PrePersist
    protected void onCreate() {
        changedAt = LocalDateTime.now();
    }
}