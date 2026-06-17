package com.travel_ease.horel_system.service.client;

import com.travel_ease.horel_system.dto.request.ConfirmBookingRequestDto;
import com.travel_ease.horel_system.dto.request.client.HoldRoomRequestDto;
import com.travel_ease.horel_system.dto.request.client.RoomAvailabilityRequestDto;
import com.travel_ease.horel_system.dto.response.client.HotelBookingValidationResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceClient {

    @Value("${booking.hotel-service.url}")
    private String hotelServiceUrl;
    private final RestTemplate restTemplate;
    private static final String HOTEL_SERVICE = "hotelService";

    @CircuitBreaker(name = HOTEL_SERVICE , fallbackMethod = "checkAndHoldFallback")
    public boolean checkAndHold(RoomAvailabilityRequestDto request) {
        log.info("Attempting to hold rooms - id: {}, qty: {}",
                request.roomId(), request.quantity());

        String url = hotelServiceUrl + "/api/v1/rooms/internal/hold";

        HttpEntity<HoldRoomRequestDto> entity = new HttpEntity<>(HoldRoomRequestDto.builder()
                .roomId(request.roomId())
                .quantity(request.quantity())
                .checkIn(request.checkIn())
                .checkOut(request.checkOut())
                .build()
        );

        ResponseEntity<Void> response = restTemplate.postForEntity(url, entity, Void.class);
        return response.getStatusCode().is2xxSuccessful();
    }

    @CircuitBreaker(name = HOTEL_SERVICE , fallbackMethod = "validateHotelFallback")
    public HotelBookingValidationResponse validateHotel(UUID hotelId) {
        String url = hotelServiceUrl + "/api/v1/rooms/internal/" + hotelId + "/validate-booking";
        log.info("Calling Hotel Service | hotelId={}", hotelId);
        return restTemplate.getForObject(url, HotelBookingValidationResponse.class);
    }

    @CircuitBreaker(name = HOTEL_SERVICE, fallbackMethod = "releaseHoldFallback")
    public boolean releaseHold(HoldRoomRequestDto dto) {
        String url = hotelServiceUrl + "/api/v1/rooms/internal/hold-release";
        log.info("Calling Hotel Service to release hold | roomId={}", dto.roomId());
        ResponseEntity<Boolean> response = restTemplate.postForEntity(url, dto, Boolean.class);
        return Boolean.TRUE.equals(response.getBody());
    }

    @CircuitBreaker(name = HOTEL_SERVICE, fallbackMethod = "updateInventoryFallback")
    public void updateInventory(ConfirmBookingRequestDto dto) {
        String url = hotelServiceUrl + "/api/v1/rooms/internal/update-inventory";
        log.info("Updating inventory | roomId={}", dto.roomId());
        restTemplate.postForEntity(url, dto, Boolean.class);

    }


    public boolean checkAndHoldFallback(RoomAvailabilityRequestDto request, Throwable t) {
        log.error("Circuit Breaker [HOTEL_SERVICE] triggered! checkAndHold failed for RoomId: {}. Reason: {}. ErrorType: {}",
                request.roomId(), t.getMessage(), t.getClass().getSimpleName());
        return false;
    }

    public HotelBookingValidationResponse validateHotelFallback(UUID hotelId, Throwable t) {
        log.error("Circuit Breaker [HOTE_SERVICE] triggered! validateHotel failed for hotelId: {}. Reason: {} ErrorType: {}",
                hotelId, t.getMessage(), t.getClass().getSimpleName());
        return null;
    }

    public boolean releaseHoldFallback(HoldRoomRequestDto dto, Throwable t) {
        log.error("Circuit Breaker [HOTEL_SERVICE] triggered! ReleaseHold failed for RoomId: {}. Reason: {}. ErrorType: {}.",
                dto.roomId(), t.getMessage(), t.getClass().getSimpleName());
        return false;
    }

    public void updateInventoryFallback(ConfirmBookingRequestDto dto, Throwable t) {
        log.error("Circuit Breaker [HOTEL_SERVICE] triggered! UpdateInventory failed for RoomId: {}. Reason: {}. ErrorType: {}.",
                dto.roomId(), t.getMessage(), t.getClass().getSimpleName());
    }
}
