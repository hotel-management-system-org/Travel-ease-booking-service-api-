package com.travel_ease.horel_system.adviser;


import com.travel_ease.horel_system.exception.*;
import com.travel_ease.horel_system.util.StandardErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppWideExceptionHandler {

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<StandardErrorResponseDto> handleEntryNotFoundException(BookingNotFoundException ex) {
        return new ResponseEntity<StandardErrorResponseDto>(
                new StandardErrorResponseDto(404,ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(DuplicateBookingException.class)
    public ResponseEntity<StandardErrorResponseDto> handleAlreadyExistsException(DuplicateBookingException ex) {
        return new ResponseEntity<StandardErrorResponseDto>(
                new StandardErrorResponseDto(409,ex.getMessage()),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(BookingException.class)
    public ResponseEntity<StandardErrorResponseDto> handleAlreadyExistsException(BookingException ex) {
        return new ResponseEntity<StandardErrorResponseDto>(
                new StandardErrorResponseDto(409,ex.getMessage()),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(HotelServiceException.class)
    public ResponseEntity<StandardErrorResponseDto> handleAlreadyExistsException(HotelServiceException ex) {
        return new ResponseEntity<StandardErrorResponseDto>(
                new StandardErrorResponseDto(409,ex.getMessage()),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(InvalidBookingDateException.class)
    public ResponseEntity<StandardErrorResponseDto> handleAlreadyExistsException(InvalidBookingDateException ex) {
        return new ResponseEntity<StandardErrorResponseDto>(
                new StandardErrorResponseDto(400,ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(InvalidBookingStateException.class)
    public ResponseEntity<StandardErrorResponseDto> handleAlreadyExistsException(InvalidBookingStateException ex) {
        return new ResponseEntity<StandardErrorResponseDto>(
                new StandardErrorResponseDto(400,ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(RoomNotAvailableException.class)
    public ResponseEntity<StandardErrorResponseDto> handleAlreadyExistsException(RoomNotAvailableException ex) {
        return new ResponseEntity<StandardErrorResponseDto>(
                new StandardErrorResponseDto(404,ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }
}
