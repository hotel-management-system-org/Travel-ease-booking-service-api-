package com.travel_ease.horel_system.exception;

public class InvalidBookingDateException extends RuntimeException {
    public InvalidBookingDateException(String message) {
        super(message);
    }
}