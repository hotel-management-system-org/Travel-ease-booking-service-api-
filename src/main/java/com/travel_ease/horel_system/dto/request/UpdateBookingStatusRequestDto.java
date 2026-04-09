package com.travel_ease.horel_system.dto.request;

import com.travel_ease.horel_system.enums.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBookingStatusRequestDto {
    @NotNull(message = "Status is required")
    private BookingStatus status;

    private String remarks;
}
