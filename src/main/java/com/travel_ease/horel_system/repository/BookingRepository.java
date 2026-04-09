package com.travel_ease.horel_system.repository;

import com.travel_ease.horel_system.entity.Booking;
import com.travel_ease.horel_system.enums.BookingStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    @Query("""
      SELECT COUNT(b) > 0
      FROM Booking b
      WHERE b.checkIn <= :checkIn
      AND b.checkOut >= :checkOut
      AND b.roomId = :roomId
      AND b.status IN ('PENDING','CONFIRMED')
      """)
    boolean existsOverlappingBooking(@NotNull @FutureOrPresent LocalDate checkIn, @NotNull @Future LocalDate checkOut, UUID roomId);

    @Query("""
      SELECT COUNT(*) FROM Booking
     """)
    long countAllBookings();

    @Query("""
    SELECT b FROM Booking b
    """)
    Page<Booking> searchAllBookings(Pageable pageable);

    @Query("""
    SELECT b FROM Booking b WHERE b.userId = :userId
    """)
    Page<Booking> findByUserId(UUID userId, Pageable pageable);

    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.checkIn <= :endDate
        AND b.checkOut >= :startDate
        ORDER BY b.checkIn ASC
        """)
    Page<Booking> findBookingByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);
}
