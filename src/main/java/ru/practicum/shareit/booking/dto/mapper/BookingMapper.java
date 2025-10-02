package ru.practicum.shareit.booking.dto.mapper;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.RequestBookingCreateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;


public final class BookingMapper {

    public static Booking toBooking(RequestBookingCreateDto booking, User user, Item item) {
        Booking createdBooking = new Booking();
        createdBooking.setStart(booking.getStart());
        createdBooking.setEnd(booking.getEnd());
        createdBooking.setStatus(BookingStatus.WAITING);
        createdBooking.setBooker(user);
        createdBooking.setItem(item);
        return createdBooking;
    }

    public static BookingCreateDto toBookingCreateDto(Booking booking) {
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setStart(booking.getStart());
        bookingCreateDto.setEnd(booking.getEnd());
        bookingCreateDto.setStatus(booking.getStatus());

        UserDto userDto = new UserDto();
        userDto.setId(booking.getBooker().getId());
        userDto.setName(booking.getBooker().getName());
        bookingCreateDto.setBooker(userDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(booking.getItem().getId());
        itemDto.setName(booking.getItem().getName());
        bookingCreateDto.setItem(itemDto);

        return bookingCreateDto;
    }

    public static List<BookingCreateDto> toListBookingDto(List<Booking> booking) {
        return booking.stream()
                .map(BookingMapper::toBookingCreateDto)
                .toList();
    }
}
