package com.travel_ease.horel_system.dto.response;

import com.travel_ease.horel_system.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponseDto {
    private UUID id;
    private UUID userId;
    private UUID hotelId;
    private UUID roomId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer totalNights;
    private BigDecimal totalPrice;
    private BookingStatus status;
    private String firstName;
    private String lastName;
    private String zipCode;
    private String city;
    private String address;
    private String guestEmail;
    private String guestPhone;
    private LocalDateTime createdAt;
    private UUID createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

}
