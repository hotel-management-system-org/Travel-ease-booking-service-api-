package com.travel_ease.horel_system.service.client;

import com.travel_ease.horel_system.dto.request.client.HoldRoomRequestDto;
import com.travel_ease.horel_system.dto.request.client.RoomAvailabilityRequestDto;
import com.travel_ease.horel_system.dto.response.client.HotelBookingValidationResponse;
import com.travel_ease.horel_system.exception.HotelServiceException;
import com.travel_ease.horel_system.exception.RoomNotAvailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceClient {

    @Value("${booking.hotel-service.url}")
    private String hotelServiceUrl;
    private final RestTemplate restTemplate;

    public boolean checkAndHold(RoomAvailabilityRequestDto request){
        log.info("Attempting to hold rooms - id: {}, qty: {}",
                request.roomId(), request.quantity());

        HoldRoomRequestDto holdRequest = HoldRoomRequestDto.builder()
                .roomId(request.roomId())
                .quantity(request.quantity())
                .checkIn(request.checkIn())
                .checkOut(request.checkOut())
                .build();

        HttpEntity<HoldRoomRequestDto> entity = new HttpEntity<>(holdRequest);

        String url = hotelServiceUrl + "/api/v1/rooms/internal/hold";

        try {
            ResponseEntity<Void> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Void.class

            );

            boolean success = response.getStatusCode().is2xxSuccessful();

            log.info("Room hold result: {}", success ? "SUCCESS" : "FAILED");

            return success;
        }catch (HttpClientErrorException.NotFound ex){
            log.warn("Room hold failed - not available: {}", ex.getResponseBodyAsString());
            return false;
        }catch (HttpClientErrorException e) {
            log.error("Room hold failed with status {}: {}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString());
            return false;
        } catch (Exception ex) {
            log.error("Room hold failed unexpectedly", ex);
            throw new RoomNotAvailableException("Room service unavailable: " + ex.getMessage());
        }
    }

    public HotelBookingValidationResponse validateHotel(UUID hotelId) {
        System.out.println("Hotel Id: " + hotelId);

        String url = hotelServiceUrl + "/api/v1/rooms/internal/" + hotelId + "/validate-booking";

        try {
            log.info("Calling Hotel Service | hotelId={}",
                    hotelId);

            ResponseEntity<HotelBookingValidationResponse> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            null,
                            HotelBookingValidationResponse.class
                    );

            return response.getBody();

        } catch (Exception ex) {
            log.error("Hotel validation failed | hotelId={}", hotelId, ex);
            throw new HotelServiceException(
                    "Unable to validate hotel for booking"
            );
        }
    }

    public boolean releaseHold(HoldRoomRequestDto dto) {
        String url = hotelServiceUrl + "/api/v1/rooms/internal/hold-release";


        try {
            log.info("Calling Hotel Service | roomId={}",
                    dto.roomId());

            HttpEntity<HoldRoomRequestDto> entity = new HttpEntity<>(dto);

            ResponseEntity<Boolean> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            entity,
                            Boolean.class
                    );

            return Boolean.TRUE.equals(response.getBody());

        } catch (Exception ex) {
            log.error("Hotel validation failed | hotelId={}", dto.roomId(), ex);
            throw new HotelServiceException(
                    "Unable to validate hotel for booking"
            );
        }
    }
}
