package com.travel_ease.horel_system.mapper;

import com.travel_ease.horel_system.dto.response.BookingResponseDto;
import com.travel_ease.horel_system.entity.Booking;
import com.travel_ease.horel_system.entity.BookingAudit;
import com.travel_ease.horel_system.enums.BookingStatus;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class Mapper {

    public BookingResponseDto toBookingResponseDto(Booking booking){
        return BookingResponseDto.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .address(booking.getAddress())
                .hotelId(booking.getHotelId())
                .roomId(booking.getRoomId())
                .checkIn(booking.getCheckIn())
                .checkOut(booking.getCheckOut())
                .totalPrice(booking.getTotalPrice())
                .totalNights(booking.getTotalNights())
                .status(booking.getStatus())
                .firstName(booking.getFirstName())
                .lastName(booking.getLastName())
                .zipCode(booking.getZipCode())
                .city(booking.getCity())
                .guestEmail(booking.getGuestEmail())
                .guestPhone(booking.getGuestPhone())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .createdBy(booking.getCreatedBy())
                .updatedBy(booking.getUpdatedBy())
                .build();
    }


    public BookingAudit createAuditRecord(Booking booking,
                                          BookingStatus oldStatus,
                                          @NotNull(message = "Status is required")
                                          BookingStatus newStatus,
                                          String action,
                                          UUID userId,
                                          String remarks)
    {
        return BookingAudit.builder()
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedBy(userId.toString())
                .remarks(remarks)
                .action(action)
                .booking(booking)
                .build();

    }
}
