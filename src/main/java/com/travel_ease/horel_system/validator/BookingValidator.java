package com.travel_ease.horel_system.validator;

import com.travel_ease.horel_system.entity.Booking;
import com.travel_ease.horel_system.enums.BookingStatus;
import com.travel_ease.horel_system.exception.InvalidBookingStateException;
import org.springframework.stereotype.Component;

@Component
public class BookingValidator {
    
    public void validateBookingIsActive(Booking booking) {
        if (booking.getStatus().equals(BookingStatus.DELETE)) {
            throw new InvalidBookingStateException("Booking has been deleted");
        }
    }
    
    public void validateBookingIsCancellable(Booking booking) {
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new InvalidBookingStateException("Booking is already cancelled");
        }
    }
}