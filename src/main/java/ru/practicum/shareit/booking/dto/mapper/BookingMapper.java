package ru.practicum.shareit.booking.dto.mapper;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.RequestBookingCreateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


public final class BookingMapper {

    public static Booking toBooking(RequestBookingCreateDto booking, User user, Item item) {

        Booking createdBooking = new Booking();
        createdBooking.setStart(booking.getStart() != null ? booking.getStart() : null);
        createdBooking.setEnd(booking.getEnd() != null ? booking.getEnd() : null);
        createdBooking.setStatus(BookingStatus.WAITING);
        if (user != null) {
            createdBooking.setBooker(user);
        }
        if (item != null) {
            createdBooking.setItem(item);
        }
        return createdBooking;
    }

    public static BookingCreateDto toBookingCreateDto(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Бронирование не найдено");
        }

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setId(booking.getId());
        bookingCreateDto.setStart(booking.getStart());
        bookingCreateDto.setEnd(booking.getEnd());
        bookingCreateDto.setStatus(booking.getStatus());

        if (booking.getBooker() != null) {
            UserDto userDto = new UserDto();
            userDto.setId(booking.getBooker().getId());
            userDto.setName(booking.getBooker().getName());
            bookingCreateDto.setBooker(userDto);
        } else {
            bookingCreateDto.setBooker(null);
        }

        if (booking.getItem() != null) {
            ItemDto itemDto = new ItemDto();
            itemDto.setId(booking.getItem().getId());
            itemDto.setName(booking.getItem().getName());
            bookingCreateDto.setItem(itemDto);
        } else {
            bookingCreateDto.setItem(null);
        }

        return bookingCreateDto;
    }

    public static List<BookingCreateDto> toListBookingDto(List<Booking> booking) {
        if (booking == null) {
            return Collections.emptyList();
        }

        return booking.stream()
                .filter(Objects::nonNull)
                .map(BookingMapper::toBookingCreateDto)
                .toList();
    }
}
