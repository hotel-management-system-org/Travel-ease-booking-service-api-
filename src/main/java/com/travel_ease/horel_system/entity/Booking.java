package com.travel_ease.horel_system.entity;

import com.travel_ease.horel_system.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "bookings",
        indexes = {
                @Index(name = "idx_room_id", columnList = "room_id"),
                @Index(name = "idx_user_id", columnList = "user_id"),
                @Index(name = "idx_check_in", columnList = "check_in"),
                @Index(name = "idx_check_out", columnList = "check_out"),
                @Index(name = "idx_status", columnList = "status"),
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_idempotency_key", columnNames = "idempotency_key")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id",columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID userId;

    @Column(name = "room_id", nullable = false, columnDefinition = "BINARY(16)")
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID roomId;

    @Column(name = "hotel_id", nullable = false, columnDefinition = "BINARY(16)")
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID hotelId;

    @Column(name = "check_in", nullable = false)
    private LocalDate checkIn;

    @Column(name = "check_out", nullable = false)
    private LocalDate checkOut;

    @Column(name = "total_nights", nullable = false)
    private Integer totalNights;

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BookingStatus status;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "guest_email", length = 100)
    private String guestEmail;

    @Column(name = "guest_phone", length = 20)
    private String guestPhone;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "zip_code", length = 100)
    private String zipCode;

    @Column(name = "address", length = 100)
    private String address;

    // Optimistic Locking
    @Version
    @Column(name = "version")
    private Long version;

    // Audit Fields
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false, updatable = false, length = 50)
    private UUID createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "booking")
    private List<BookingAudit> bookingAudit;

    @OneToOne(mappedBy = "booking")
    private IdempotencyRecord idempotencyRecord;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }



}