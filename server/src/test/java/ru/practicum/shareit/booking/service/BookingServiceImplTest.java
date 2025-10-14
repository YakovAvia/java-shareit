package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.RequestBookingCreateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void createBooking_whenAllValid_thenSaveBooking() {
        long userId = 1L;
        long ownerId = 2L;
        long itemId = 1L;

        User booker = new User();
        booker.setId(userId);

        User owner = new User();
        owner.setId(ownerId);

        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(true);
        item.setUser(owner);

        RequestBookingCreateDto bookingDto = new RequestBookingCreateDto();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.WAITING);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        assertNotNull(bookingService.createBooking(bookingDto, userId));
    }

    @Test
    void createBooking_whenItemNotAvailable_thenThrowValidationException() {
        long userId = 1L;
        long itemId = 1L;
        User booker = new User();
        booker.setId(userId);
        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(false);

        RequestBookingCreateDto bookingDto = new RequestBookingCreateDto();
        bookingDto.setItemId(itemId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingDto, userId));
    }

    @Test
    void updateBookingStatus_whenApproved_thenSetStatusApproved() {
        long userId = 1L;
        long bookingId = 1L;
        User owner = new User();
        owner.setId(userId);
        Item item = new Item();
        item.setUser(owner);
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        bookingService.updateBookingStatus(userId, bookingId, true);

        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }

    @Test
    void updateBookingStatus_whenNotOwner_thenThrowValidationException() {
        long userId = 1L;
        long ownerId = 2L;
        long bookingId = 1L;
        User owner = new User();
        owner.setId(ownerId);
        Item item = new Item();
        item.setUser(owner);
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.updateBookingStatus(userId, bookingId, true));
    }
}
