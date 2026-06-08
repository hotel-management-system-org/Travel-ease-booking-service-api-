package com.travel_ease.horel_system.service.impl;

import com.travel_ease.horel_system.dto.request.CreateBookingRequestDto;
import com.travel_ease.horel_system.dto.request.UpdateBookingStatusRequestDto;
import com.travel_ease.horel_system.dto.request.client.HoldRoomRequestDto;
import com.travel_ease.horel_system.dto.request.client.RoomAvailabilityRequestDto;
import com.travel_ease.horel_system.dto.response.BookingResponseDto;
import com.travel_ease.horel_system.dto.response.client.HotelBookingValidationResponse;
import com.travel_ease.horel_system.dto.response.paginate.BookingPaginateResponseDto;
import com.travel_ease.horel_system.entity.Booking;
import com.travel_ease.horel_system.entity.IdempotencyRecord;
import com.travel_ease.horel_system.enums.BookingStatus;
import com.travel_ease.horel_system.exception.BookingException;
import com.travel_ease.horel_system.exception.BookingNotFoundException;
import com.travel_ease.horel_system.exception.RoomNotAvailableException;
import com.travel_ease.horel_system.mapper.Mapper;
import com.travel_ease.horel_system.repository.BookingRepository;
import com.travel_ease.horel_system.repository.IdempotencyRepository;
import com.travel_ease.horel_system.service.BookingService;
import com.travel_ease.horel_system.service.client.HotelServiceClient;
import com.travel_ease.horel_system.service.common.CommonTransactionService;
import com.travel_ease.horel_system.validator.BookingValidator;
import com.travel_ease.horel_system.validator.DateValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final IdempotencyRepository idempotencyRepository;
    private final DateValidator dateValidator;
    private final HotelServiceClient hotelServiceClient;
    private final Mapper mapper;
    private final CommonTransactionService commonTransactionService;
    private final BookingValidator bookingValidator;


    @Override
    public BookingResponseDto createBooking(CreateBookingRequestDto request, UUID userId, String idempotencyKey) {
          if (idempotencyKey != null){
              Optional<Booking> exists = idempotencyRepository.findByIdempotencyKey(idempotencyKey)
                      .map(IdempotencyRecord::getBooking);


              if (exists.isPresent()){
                  log.info("Idempotency key {} matched. Returning existing booking.", idempotencyKey);
                  return getBookingById(exists.get().getId());
              }
          }

        dateValidator.validateBookingDates(request.getCheckIn(), request.getCheckOut());

        HotelBookingValidationResponse hotelValidate = hotelServiceClient.validateHotel(request.getHotelId());

        if (!hotelValidate.isBookingAllowed()) {
            throw new BookingException("Booking not allowed for this hotel.");
        }

        RoomAvailabilityRequestDto availabilityRequest = RoomAvailabilityRequestDto.builder()
                .roomId(request.getRoomId())
                .checkIn(request.getCheckIn())
                .checkOut(request.getCheckOut())
                .quantity(request.getQuantity())
                .build();

        boolean available = hotelServiceClient.checkAndHold(availabilityRequest);

        if (!available){
            throw new RoomNotAvailableException("Room not available or inventory hold failed.");
        }

        try {
            int totalNights = (int) ChronoUnit.DAYS.between(request.getCheckIn(),request.getCheckOut());
            return commonTransactionService.saveBookingAndIdempotency(request,userId,idempotencyKey,totalNights);

        }catch (Exception ex){
            log.error("Database save failed for booking. Rolling back hotel hold via compensating call.", ex);

           HoldRoomRequestDto rollbackHold = HoldRoomRequestDto.builder()
                    .roomId(request.getRoomId()).
                    quantity(request.getQuantity()).
                    checkIn(request.getCheckIn()).
                    checkOut(request.getCheckOut()).
                    build();
           hotelServiceClient.releaseHold(rollbackHold);
            throw new BookingException("Booking failed due to an internal system error. Your hold has been released.");

        }

    }

    @Override
    public BookingResponseDto getBookingById(UUID id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));
        return mapper.toBookingResponseDto(booking);
    }

    @Override
    public BookingPaginateResponseDto getAllBookings(int page, int size) {

        Page<Booking> bookingPage =
                bookingRepository.findAll(PageRequest.of(page, size));

        return BookingPaginateResponseDto.builder()
                .dataCount(bookingPage.getTotalElements())
                .dataList(
                        bookingPage.getContent()
                                .stream()
                                .map(mapper::toBookingResponseDto)
                                .collect(Collectors.toList())
                )
                .build();
    }


    @Override
    public BookingPaginateResponseDto getBookingsByUser(UUID userId, int page, int size) {

        Page<Booking> bookingPage =
                bookingRepository.findByUserId(
                        userId,
                        PageRequest.of(page, size)
                );

        return BookingPaginateResponseDto.builder()
                .dataCount(bookingPage.getTotalElements())
                .dataList(
                        bookingPage.getContent()
                                .stream()
                                .map(mapper::toBookingResponseDto)
                                .collect(Collectors.toList())
                )
                .build();
    }


    @Override
    public BookingPaginateResponseDto getBookingsByStatus(int page, int size, BookingStatus status) {

        Page<Booking> bookingPage =
                bookingRepository.findByStatus(status, PageRequest.of(page, size));

        return BookingPaginateResponseDto.builder()
                .dataCount(bookingPage.getTotalElements())
                .dataList(
                        bookingPage.getContent()
                                .stream()
                                .map(mapper::toBookingResponseDto)
                                .collect(Collectors.toList())
                )
                .build();
    }


    @Override
    public BookingPaginateResponseDto getBookingsByDateRange(int page, int size, LocalDate startDate, LocalDate endDate) {

        Page<Booking> bookingPage =
                bookingRepository.findBookingByDateRange(
                        startDate,
                        endDate,
                        PageRequest.of(page, size)
                );

        return BookingPaginateResponseDto.builder()
                .dataCount(bookingPage.getTotalElements())
                .dataList(
                        bookingPage.getContent()
                                .stream()
                                .map(mapper::toBookingResponseDto)
                                .collect(Collectors.toList())
                )
                .build();
    }



    @Override
    public BookingResponseDto confirmBooking(UUID id, UUID userId) {
        UpdateBookingStatusRequestDto request = UpdateBookingStatusRequestDto.builder()
                .status(BookingStatus.CONFIRMED)
                .remarks("Booking confirmed")
                .build();

        return commonTransactionService.updateBookingStatus(id, request, userId);
    }

    @Override
    public BookingResponseDto cancelBooking(UUID id, UUID userId, String reason) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));

        bookingValidator.validateBookingIsCancellable(booking);
        UpdateBookingStatusRequestDto updateRequest = UpdateBookingStatusRequestDto.builder()
                .status(BookingStatus.CANCELLED)
                .remarks("Booking cancelled")
                .build();
        BookingResponseDto response = commonTransactionService.updateBookingStatus(id, updateRequest, userId);


        HoldRoomRequestDto releaseHoldRequest = HoldRoomRequestDto.builder()
                .roomId(booking.getRoomId())
                .checkIn(booking.getCheckIn())
                .checkOut(booking.getCheckOut())
                .build();

        try {
            boolean isReleasedHold = hotelServiceClient.releaseHold(releaseHoldRequest);

            if (!isReleasedHold) {
                throw new BookingException("Hotel service failed to release the room hold.");
            }
        }catch (Exception ex){
            log.error("Failed to release room hold in Hotel Service for booking: {}. Rolling back DB state.", id, ex);
            UpdateBookingStatusRequestDto rollbackRequest = UpdateBookingStatusRequestDto.builder()
                    .status(BookingStatus.CONFIRMED)
                    .remarks("Rollback: Failed to release hotel hold -> " + ex.getMessage())
                    .build();
            commonTransactionService.updateBookingStatus(id, rollbackRequest, userId);
            throw new BookingException("Cancellation failed because hotel inventory could not be updated. Please try again.");
        }

      return response;

    }

    @Override
    public void deleteBooking(UUID id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));

        booking.setStatus(BookingStatus.DELETE);
        bookingRepository.save(booking);
        log.info("Booking {} soft deleted", id);
    }

}
