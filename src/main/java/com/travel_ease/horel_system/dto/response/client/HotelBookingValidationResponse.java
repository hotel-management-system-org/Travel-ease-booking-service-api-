package com.travel_ease.horel_system.dto.response.client;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelBookingValidationResponse {
    private String hotelId;
    private String hotelName;
    private boolean status;
    private boolean bookingAllowed;


}
