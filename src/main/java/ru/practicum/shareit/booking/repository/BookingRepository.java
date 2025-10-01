package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_Id(Long id, Sort sort);

    List<Booking> findByBooker_IdAndStatus(Long userId, BookingStatus status, Sort sort);

    List<Booking> findByBooker_IdAndEndBefore(Long userId, LocalDateTime end, Sort sort);

    List<Booking> findByBooker_IdAndStartAfter(Long userId, LocalDateTime end, Sort sort);

    @Query("SELECT b FROM Booking as b " +
            "WHERE b.booker.id = :userId AND :now BETWEEN b.start AND b.end " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentByBooker(Long userId, LocalDateTime now);

    List<Booking> findByItem_User_Id(Long userId, Sort sort);

    List<Booking> findByItem_User_IdAndStatus(Long itemId, BookingStatus status, Sort sort);

    List<Booking> findByItem_User_IdAndEndBefore(Long userId, LocalDateTime end, Sort sort);

    List<Booking> findByItem_User_IdAndStartAfter(Long userId, LocalDateTime start, Sort sort);

    @Query("SELECT b FROM Booking as b " +
            "where b.item.user.id = :userId AND :now BETWEEN b.start AND b.end " +
            "ORDER BY b.start DESC")
    List<Booking> findByItemUser(Long userId, LocalDateTime now);

    List<Booking> findByBookerIdAndItemIdAndStatusAndEndBefore(
            Long bookerId,
            Long itemId,
            BookingStatus status,
            LocalDateTime end
    );

    @Query("SELECT b FROM Booking as b " +
            "where b.item.id = :itemId " +
            "AND b.start < :now " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.start DESC " +
            "LIMIT 1")
    Booking findLastBooking(Long itemId, LocalDateTime now);

    @Query("SELECT b from Booking as b " +
            "where b.item.id = :itemId " +
            "AND b.start > :now " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.end ASC " +
            "LIMIT 1")
    Booking findNextBooking(Long itemId, LocalDateTime now);
}
