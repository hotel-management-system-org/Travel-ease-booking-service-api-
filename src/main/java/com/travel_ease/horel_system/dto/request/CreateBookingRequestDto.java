package com.travel_ease.horel_system.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBookingRequestDto {

    @NotNull(message = "Room type ID is required")
    private UUID roomId;

    @Min(value = 1, message = "At least 1 room is required")
    private int quantity;

    private BigDecimal totalPrice;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String address;

    @NotBlank
    private String zipCode;

    @NotBlank
    private String city;

    @NotNull
    private UUID hotelId;

    @Email
    private String guestEmail;

    @NotBlank
    private String guestPhone;

    @NotNull
    @FutureOrPresent
    private LocalDate checkIn;

    @NotNull
    @Future
    private LocalDate checkOut;
}
