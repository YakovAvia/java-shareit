package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_IdOrderByStartDesc(Long id);
    List<Booking> findByBooker_IdAndStatusOrderByStartDesc(Long userId, BookingStatus status);
    List<Booking> findByBooker_IdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime end);
    List<Booking> findByBooker_IdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime end);

    @Query("SELECT b FROM Booking as b " +
            "WHERE b.booker.id = :userId AND :now BETWEEN b.start AND b.end " +
            "ORDER BY b.start DESC ")
    List<Booking> findCurrentByBooker(Long userId, LocalDateTime now);
}
