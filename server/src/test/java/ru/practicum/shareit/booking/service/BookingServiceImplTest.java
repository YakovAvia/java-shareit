package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.RequestBookingCreateDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
    void createBooking_WithValidData_ShouldCreateBooking() {

        Long userId = 1L;
        Long itemId = 1L;

        RequestBookingCreateDto requestDto = new RequestBookingCreateDto();
        requestDto.setItemId(itemId);
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        User booker = new User();
        booker.setId(userId);

        User owner = new User();
        owner.setId(2L);

        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(true);
        item.setUser(owner);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingCreateDto result = bookingService.createBooking(requestDto, userId);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBooking_WhenUserNotFound_ShouldThrowException() {

        Long userId = 999L;
        RequestBookingCreateDto requestDto = new RequestBookingCreateDto();
        requestDto.setItemId(1L);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(requestDto, userId));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_WhenItemNotFound_ShouldThrowException() {

        Long userId = 1L;
        Long itemId = 999L;

        RequestBookingCreateDto requestDto = new RequestBookingCreateDto();
        requestDto.setItemId(itemId);

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(requestDto, userId));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_WhenBookingOwnItem_ShouldThrowException() {

        Long userId = 1L;
        Long itemId = 1L;

        RequestBookingCreateDto requestDto = new RequestBookingCreateDto();
        requestDto.setItemId(itemId);

        User owner = new User();
        owner.setId(userId);

        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(true);
        item.setUser(owner);

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(requestDto, userId));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_WhenItemNotAvailable_ShouldThrowException() {

        Long userId = 1L;
        Long itemId = 1L;

        RequestBookingCreateDto requestDto = new RequestBookingCreateDto();
        requestDto.setItemId(itemId);

        User booker = new User();
        booker.setId(userId);

        User owner = new User();
        owner.setId(2L);

        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(false);
        item.setUser(owner);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(requestDto, userId));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateBookingStatus_WithApproved_ShouldUpdateStatus() {

        Long userId = 1L;
        Long bookingId = 1L;
        boolean approved = true;

        User owner = new User();
        owner.setId(userId);

        Item item = new Item();
        item.setUser(owner);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);

        Booking updatedBooking = new Booking();
        updatedBooking.setId(bookingId);
        updatedBooking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(updatedBooking);

        BookingCreateDto result = bookingService.updateBookingStatus(userId, bookingId, approved);

        assertNotNull(result);
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void updateBookingStatus_WithRejected_ShouldUpdateStatus() {
        Long userId = 1L;
        Long bookingId = 1L;
        boolean approved = false;

        User owner = new User();
        owner.setId(userId);

        Item item = new Item();
        item.setUser(owner);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);

        Booking updatedBooking = new Booking();
        updatedBooking.setId(bookingId);
        updatedBooking.setStatus(BookingStatus.REJECTED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(updatedBooking);

        BookingCreateDto result = bookingService.updateBookingStatus(userId, bookingId, approved);

        assertNotNull(result);
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void updateBookingStatus_WhenBookingNotFound_ShouldThrowException() {

        Long userId = 1L;
        Long bookingId = 999L;
        boolean approved = true;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.updateBookingStatus(userId, bookingId, approved));
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateBookingStatus_WhenNotWaitingStatus_ShouldThrowException() {

        Long userId = 1L;
        Long bookingId = 1L;
        boolean approved = true;

        User owner = new User();
        owner.setId(userId);

        Item item = new Item();
        item.setUser(owner);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setItem(item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.updateBookingStatus(userId, bookingId, approved));
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateBookingStatus_WhenNotOwner_ShouldThrowException() {

        Long userId = 1L;
        Long bookingId = 1L;
        boolean approved = true;

        User owner = new User();
        owner.setId(2L);

        User currentUser = new User();
        currentUser.setId(userId);

        Item item = new Item();
        item.setUser(owner);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.updateBookingStatus(userId, bookingId, approved));
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void getBooking_WhenUserIsOwner_ShouldReturnBooking() {

        Long userId = 1L;
        Long bookingId = 1L;

        User owner = new User();
        owner.setId(userId);

        Item item = new Item();
        item.setUser(owner);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingCreateDto result = bookingService.getBooking(userId, bookingId);

        assertNotNull(result);
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void getBooking_WhenUserIsBooker_ShouldReturnBooking() {

        Long userId = 1L;
        Long bookingId = 1L;

        User booker = new User();
        booker.setId(userId);

        User owner = new User();
        owner.setId(2L);

        Item item = new Item();
        item.setUser(owner);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingCreateDto result = bookingService.getBooking(userId, bookingId);

        assertNotNull(result);
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void getBooking_WhenUserNotRelated_ShouldThrowException() {

        Long userId = 1L;
        Long bookingId = 1L;

        User otherUser = new User();
        otherUser.setId(3L);

        User owner = new User();
        owner.setId(2L);

        User booker = new User();
        booker.setId(2L);

        Item item = new Item();
        item.setUser(owner);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.getBooking(userId, bookingId));
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void getBooking_WhenBookingNotFound_ShouldThrowException() {

        Long userId = 1L;
        Long bookingId = 999L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(userId, bookingId));
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void getAllBookingsToUser_WithAllState_ShouldReturnAllBookings() {

        Long userId = 1L;
        String state = "ALL";
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        User user = new User();
        user.setId(userId);

        Booking booking1 = new Booking();
        booking1.setId(1L);

        Booking booking2 = new Booking();
        booking2.setId(2L);

        List<Booking> bookings = Arrays.asList(booking1, booking2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBooker_Id(userId, sort)).thenReturn(bookings);

        List<BookingCreateDto> result = bookingService.getAllBookingsToUser(userId, state);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findByBooker_Id(userId, sort);
    }

    @Test
    void getAllBookingsToUser_WithInvalidState_ShouldThrowException() {

        Long userId = 1L;
        String state = "INVALID";

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(ValidationException.class, () -> bookingService.getAllBookingsToUser(userId, state));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getAllBookingsToUser_WhenUserNotFound_ShouldThrowException() {

        Long userId = 999L;
        String state = "ALL";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsToUser(userId, state));
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, never()).findByBooker_Id(anyLong(), any(Sort.class));
    }

    @Test
    void getAllItemBookingToUser_WithAllState_ShouldReturnAllItemBookings() {

        Long userId = 1L;
        String state = "ALL";
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        Item item1 = new Item();
        item1.setId(1L);

        Item item2 = new Item();
        item2.setId(2L);

        List<Item> userItems = Arrays.asList(item1, item2);

        Booking booking1 = new Booking();
        booking1.setId(1L);

        Booking booking2 = new Booking();
        booking2.setId(2L);

        List<Booking> bookings = Arrays.asList(booking1, booking2);

        when(itemRepository.findAllByUser_Id(userId)).thenReturn(userItems);
        when(bookingRepository.findByItem_User_Id(userId, sort)).thenReturn(bookings);

        List<BookingCreateDto> result = bookingService.getAllItemBookingToUser(userId, state);

        assertNotNull(result);
        verify(itemRepository, times(1)).findAllByUser_Id(userId);
        verify(bookingRepository, times(1)).findByItem_User_Id(userId, sort);
    }

    @Test
    void getAllItemBookingToUser_WhenUserHasNoItems_ShouldThrowException() {

        Long userId = 1L;
        String state = "ALL";

        when(itemRepository.findAllByUser_Id(userId)).thenReturn(Arrays.asList());

        assertThrows(NotFoundException.class, () -> bookingService.getAllItemBookingToUser(userId, state));
        verify(itemRepository, times(1)).findAllByUser_Id(userId);
        verify(bookingRepository, never()).findByItem_User_Id(anyLong(), any(Sort.class));
    }

    @Test
    void getAllItemBookingToUser_WithInvalidState_ShouldThrowException() {

        Long userId = 1L;
        String state = "INVALID";

        Item item = new Item();
        item.setId(1L);

        when(itemRepository.findAllByUser_Id(userId)).thenReturn(Arrays.asList(item));

        assertThrows(ValidationException.class, () -> bookingService.getAllItemBookingToUser(userId, state));
        verify(itemRepository, times(1)).findAllByUser_Id(userId);
    }
}
