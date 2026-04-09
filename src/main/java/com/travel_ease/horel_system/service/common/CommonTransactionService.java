package com.travel_ease.horel_system.service.common;

import com.travel_ease.horel_system.dto.request.UpdateBookingStatusRequestDto;
import com.travel_ease.horel_system.dto.response.BookingResponseDto;
import com.travel_ease.horel_system.entity.Booking;
import com.travel_ease.horel_system.entity.BookingAudit;
import com.travel_ease.horel_system.enums.BookingStatus;
import com.travel_ease.horel_system.exception.BookingException;
import com.travel_ease.horel_system.exception.BookingNotFoundException;
import com.travel_ease.horel_system.mapper.Mapper;
import com.travel_ease.horel_system.repository.BookingAuditRepository;
import com.travel_ease.horel_system.repository.BookingRepository;
import com.travel_ease.horel_system.validator.BookingValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommonTransactionService {

    private final BookingRepository bookingRepository;
    private final Mapper mapper;
    private final BookingValidator bookingValidator;
    private final BookingAuditRepository bookingAuditRepository;

    @Transactional
    public BookingResponseDto updateBookingStatus(UUID id, UpdateBookingStatusRequestDto request, UUID userId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));

        bookingValidator.validateBookingIsActive(booking);

        BookingStatus oldStatus = booking.getStatus();

        try {
            booking.setStatus(request.getStatus());
            booking.setUpdatedBy(userId.toString());
            booking = bookingRepository.save(booking);

            BookingAudit bookingAudit = mapper.createAuditRecord(booking, oldStatus, request.getStatus(), "STATUS_CHANGE", userId, request.getRemarks());
            bookingAuditRepository.save(bookingAudit);

            log.info("Booking {} status updated from {} to {}", id, oldStatus, request.getStatus());


        }catch (OptimisticLockingFailureException e){
            log.error("Concurrent modification detected for booking {}", id);
            throw new BookingException("Booking was modified by another user. Please refresh and try again.");
        }

        return mapper.toBookingResponseDto(booking);
    }
}
