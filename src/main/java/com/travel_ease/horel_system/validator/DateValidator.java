package com.travel_ease.horel_system.validator;

import com.travel_ease.horel_system.exception.InvalidBookingDateException;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class DateValidator {

    public void validateBookingDates(LocalDate checkIn,LocalDate checkOut){

        if (!checkIn.isBefore(checkOut)) {
            throw new InvalidBookingDateException("Check-out date must be after check-in date");
        }


        if (checkIn.isBefore(LocalDate.now())) {
            throw new InvalidBookingDateException("Check-in date cannot be in the past");
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights < 1) {
            throw new InvalidBookingDateException("Minimum 1 night stay is required");
        }
    }
}
