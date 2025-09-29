package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingCreateDto bookingDto);

    BookingDto updateBookingStatus(Long userID, Long bookingId, boolean approved);

    BookingDto getBooking(Long userID, Long bookingId);

    List<BookingDto> getAllBookingsToUser(Long userID, String state);
}
