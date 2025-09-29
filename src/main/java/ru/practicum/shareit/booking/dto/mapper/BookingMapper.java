package ru.practicum.shareit.booking.dto.mapper;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;


public final class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStart(booking.getStart());
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    public static Booking toBooking(BookingCreateDto booking, User user, Item item) {
        Booking createdBooking = new Booking();
        createdBooking.setStart(booking.getStart());
        createdBooking.setEnd(booking.getEnd());
        createdBooking.setBooker(user);
        createdBooking.setItem(item);
        return createdBooking;
    }

    public static List<BookingDto> toListBookingDto(List<Booking> booking) {
        return booking.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }
}
