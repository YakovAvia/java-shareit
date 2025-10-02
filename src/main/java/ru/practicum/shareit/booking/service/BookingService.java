package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.RequestBookingCreateDto;

import java.util.List;

public interface BookingService {

    BookingCreateDto createBooking(RequestBookingCreateDto bookingDto, Long userId);

    BookingCreateDto updateBookingStatus(Long userID, Long bookingId, boolean approved);

    BookingCreateDto getBooking(Long userID, Long bookingId);

    List<BookingCreateDto> getAllBookingsToUser(Long userId, String state);

    List<BookingCreateDto> getAllItemBookingToUser(Long userId, String state);
}
