package com.travel_ease.horel_system.dto.request.client;

import lombok.Builder;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record HoldRoomRequestDto(
    UUID roomId,
    int quantity,
    LocalDate checkIn,
    LocalDate checkOut

) {}