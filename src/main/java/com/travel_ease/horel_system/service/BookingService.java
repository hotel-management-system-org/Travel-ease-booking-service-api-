package com.travel_ease.horel_system.service;

import com.travel_ease.horel_system.dto.request.CreateBookingRequestDto;
import com.travel_ease.horel_system.dto.response.BookingResponseDto;
import com.travel_ease.horel_system.dto.response.paginate.BookingPaginateResponseDto;
import com.travel_ease.horel_system.enums.BookingStatus;
import java.time.LocalDate;
import java.util.UUID;

public interface BookingService {

    public BookingResponseDto createBooking(CreateBookingRequestDto request,UUID userId,String idempotencyKey);
    public BookingResponseDto getBookingById(UUID id);
    public BookingPaginateResponseDto getAllBookings(int page, int size);
    public BookingPaginateResponseDto getBookingsByUser(UUID userId,int page, int size);
    public BookingPaginateResponseDto getBookingsByStatus(int page, int size,BookingStatus status);
    public BookingPaginateResponseDto getBookingsByDateRange(int page, int size,LocalDate startDate, LocalDate endDate);
    public BookingResponseDto confirmBooking(UUID id,UUID userId);
    public BookingResponseDto cancelBooking(UUID id, UUID userId, String reason);
    public void deleteBooking(UUID id);

}
