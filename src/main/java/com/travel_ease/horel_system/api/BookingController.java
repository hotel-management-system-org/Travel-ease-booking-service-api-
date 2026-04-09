package com.travel_ease.horel_system.api;

import com.travel_ease.horel_system.dto.request.CreateBookingRequestDto;
import com.travel_ease.horel_system.dto.response.BookingResponseDto;
import com.travel_ease.horel_system.enums.BookingStatus;
import com.travel_ease.horel_system.service.BookingService;
import com.travel_ease.horel_system.util.JwtUtil;
import com.travel_ease.horel_system.util.StandardResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/bookings-service/api/v1/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    
    private final BookingService bookingService;
    private final JwtUtil jwtUtil;

    @PostMapping("/user/create")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','CUSTOMER')")
    public ResponseEntity<StandardResponseDto> createBooking(
        @Valid @RequestBody CreateBookingRequestDto request,
        @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
        Authentication authentication
    ) {
        System.out.println("Idempotency-Key: " + idempotencyKey);
        String userId = jwtUtil.extractUserId(authentication);
        
        log.info("Creating booking for , user: {}",  userId);
        
        BookingResponseDto response = bookingService.createBooking(request, UUID.fromString(userId), idempotencyKey);

        return new ResponseEntity<>(
                new StandardResponseDto(
                        201, "Booking Created!", response
                ),
                HttpStatus.CREATED
        );
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','CUSTOMER')")
    public ResponseEntity<StandardResponseDto> getBooking(
        @PathVariable UUID id
    ) {
        BookingResponseDto response = bookingService.getBookingById(id);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        200, "Booking found!", response
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/admin/find-all")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> getAllBookings(
            @RequestParam int page,
            @RequestParam int size
    ) {

        return new ResponseEntity<>(
                new StandardResponseDto(
                        200, "Booking List!", bookingService.getAllBookings(page, size)
                ),
                HttpStatus.OK
        );
    }
    
    @GetMapping("/user/my-bookings")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','CUSTOMER')")
    public ResponseEntity<StandardResponseDto> getMyBookings(
            @RequestParam int page,
            @RequestParam int size,
            Authentication authentication
    ) {
        String userId = jwtUtil.extractUserId(authentication);

        return new ResponseEntity<>(
                new StandardResponseDto(
                        200, "Booking List!", bookingService.getBookingsByUser(UUID.fromString(userId),page,size)
                ),
                HttpStatus.OK
        );
    }
    
    @GetMapping("/user/status/{status}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','CUSTOMER')")
    public ResponseEntity<StandardResponseDto> getBookingsByStatus(
            @RequestParam int page,
            @RequestParam int size,
            @PathVariable BookingStatus status

    ) {

        return new ResponseEntity<>(
                new StandardResponseDto(
                        200, "Booking List!", bookingService.getBookingsByStatus(page,size,status)
                ),
                HttpStatus.OK
        );
    }
    
    @GetMapping("/user/date-range")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','CUSTOMER')")
    public ResponseEntity<StandardResponseDto> getBookingsByDateRange(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate

    ) {

        return new ResponseEntity<>(
                new StandardResponseDto(
                        200, "Booking List!",  bookingService.getBookingsByDateRange(page,size ,startDate, endDate)
                ),
                HttpStatus.OK
        );
    }
    

    
    @PostMapping("/user/{id}/confirm")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','CUSTOMER')")
    public ResponseEntity<StandardResponseDto> confirmBooking(
        @PathVariable UUID id,
        Authentication authentication
    ) {
        String userId = jwtUtil.extractUserId(authentication);

        return new ResponseEntity<>(
                new StandardResponseDto(
                        200, "Booking status updated!", bookingService.confirmBooking(id,UUID.fromString(userId))
                ),
                HttpStatus.OK
        );
    }
    
    @PostMapping("/user/{id}/cancel")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','CUSTOMER')")
    public ResponseEntity<StandardResponseDto> cancelBooking(
        @PathVariable UUID id,
        @RequestParam(required = false) String reason,
        Authentication authentication
    ) {
        String userId = jwtUtil.extractUserId(authentication);

        return new ResponseEntity<>(
                new StandardResponseDto(
                        200, "Booking status updated!",bookingService.cancelBooking(id, UUID.fromString(userId), reason)
                ),
                HttpStatus.OK
        );
    }
    
    @DeleteMapping("/user/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> deleteBooking(
        @PathVariable UUID id
    ) {
        bookingService.deleteBooking(id);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        200, "Booking status updated!",null
                ),
                HttpStatus.OK
        );

    }
}