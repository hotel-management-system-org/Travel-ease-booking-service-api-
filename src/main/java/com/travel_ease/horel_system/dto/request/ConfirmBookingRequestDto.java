package com.travel_ease.horel_system.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ConfirmBookingRequestDto(
    @NotBlank(message = "Room ID cannot be blank")
    String roomId,
    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date must be today or a future date")
    LocalDate checkIn,
    @NotNull(message = "Check-out date is required")
    LocalDate checkOut,
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity
) {
    public ConfirmBookingRequestDto {
        if (checkIn != null && checkOut != null && !checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
    }
}