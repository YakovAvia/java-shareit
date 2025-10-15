package ru.practicum.shareit.booking.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.RequestBookingCreateDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @Test
    void createBooking_WithValidData_ShouldReturnCreatedBooking() {

        Long userId = 1L;
        RequestBookingCreateDto requestDto = new RequestBookingCreateDto();
        requestDto.setItemId(1L);

        BookingCreateDto createdBooking = new BookingCreateDto();
        createdBooking.setId(1L);
        createdBooking.setStatus(BookingStatus.valueOf("WAITING"));

        when(bookingService.createBooking(any(RequestBookingCreateDto.class), eq(userId)))
                .thenReturn(createdBooking);

        ResponseEntity<BookingCreateDto> response = bookingController.createBooking(userId, requestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdBooking, response.getBody());
        verify(bookingService, times(1)).createBooking(requestDto, userId);
    }

    @Test
    void updateBookingStatus_WithApproved_ShouldReturnUpdatedBooking() {

        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = true;

        BookingCreateDto updatedBooking = new BookingCreateDto();
        updatedBooking.setId(bookingId);
        updatedBooking.setStatus(BookingStatus.valueOf("APPROVED"));

        when(bookingService.updateBookingStatus(userId, bookingId, approved))
                .thenReturn(updatedBooking);

        ResponseEntity<BookingCreateDto> response = bookingController.updateBookingStatus(userId, bookingId, approved);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updatedBooking, response.getBody());
        verify(bookingService, times(1)).updateBookingStatus(userId, bookingId, approved);
    }

    @Test
    void updateBookingStatus_WithRejected_ShouldReturnUpdatedBooking() {

        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = false;

        BookingCreateDto updatedBooking = new BookingCreateDto();
        updatedBooking.setId(bookingId);
        updatedBooking.setStatus(BookingStatus.valueOf("REJECTED"));

        when(bookingService.updateBookingStatus(userId, bookingId, approved))
                .thenReturn(updatedBooking);

        ResponseEntity<BookingCreateDto> response = bookingController.updateBookingStatus(userId, bookingId, approved);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updatedBooking, response.getBody());
        verify(bookingService, times(1)).updateBookingStatus(userId, bookingId, approved);
    }

    @Test
    void getBooking_WhenBookingExists_ShouldReturnBooking() {

        Long userId = 1L;
        Long bookingId = 1L;

        BookingCreateDto booking = new BookingCreateDto();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.valueOf("APPROVED"));

        when(bookingService.getBooking(userId, bookingId)).thenReturn(booking);

        ResponseEntity<BookingCreateDto> response = bookingController.getBooking(userId, bookingId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(booking, response.getBody());
        verify(bookingService, times(1)).getBooking(userId, bookingId);
    }

    @Test
    void getAllBookingsToUser_WithAllState_ShouldReturnAllBookings() {

        Long userId = 1L;
        String state = "ALL";

        BookingCreateDto booking1 = new BookingCreateDto();
        booking1.setId(1L);

        BookingCreateDto booking2 = new BookingCreateDto();
        booking2.setId(2L);

        List<BookingCreateDto> expectedBookings = Arrays.asList(booking1, booking2);

        when(bookingService.getAllBookingsToUser(userId, state)).thenReturn(expectedBookings);

        ResponseEntity<List<BookingCreateDto>> response = bookingController.getAllBookingsToUser(userId, state);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(expectedBookings, response.getBody());
        verify(bookingService, times(1)).getAllBookingsToUser(userId, state);
    }

    @Test
    void getAllBookingsToUser_WithDefaultState_ShouldUseAll() {

        Long userId = 1L;

        List<BookingCreateDto> expectedBookings = Arrays.asList();

        when(bookingService.getAllBookingsToUser(userId, "ALL")).thenReturn(expectedBookings);

        ResponseEntity<List<BookingCreateDto>> response = bookingController.getAllBookingsToUser(userId, "ALL");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(bookingService, times(1)).getAllBookingsToUser(userId, "ALL");
    }

    @Test
    void getAllBookingsToUser_WithDifferentStates_ShouldReturnFilteredBookings() {
        Long userId = 1L;
        String[] states = {"WAITING", "REJECTED", "CURRENT", "PAST", "FUTURE"};

        for (String state : states) {
            List<BookingCreateDto> expectedBookings = Arrays.asList(new BookingCreateDto());
            when(bookingService.getAllBookingsToUser(userId, state)).thenReturn(expectedBookings);

            ResponseEntity<List<BookingCreateDto>> response = bookingController.getAllBookingsToUser(userId, state);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().size());
            verify(bookingService, times(1)).getAllBookingsToUser(userId, state);

            reset(bookingService);
        }
    }

    @Test
    void getAllItemBookingToUser_WithAllState_ShouldReturnAllItemBookings() {

        Long userId = 1L;
        String state = "ALL";

        BookingCreateDto booking1 = new BookingCreateDto();
        booking1.setId(1L);

        BookingCreateDto booking2 = new BookingCreateDto();
        booking2.setId(2L);

        List<BookingCreateDto> expectedBookings = Arrays.asList(booking1, booking2);

        when(bookingService.getAllItemBookingToUser(userId, state)).thenReturn(expectedBookings);

        ResponseEntity<List<BookingCreateDto>> response = bookingController.getAllItemBookingToUser(userId, state);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(expectedBookings, response.getBody());
        verify(bookingService, times(1)).getAllItemBookingToUser(userId, state);
    }

    @Test
    void getAllItemBookingToUser_WithDefaultState_ShouldUseAll() {

        Long userId = 1L;

        List<BookingCreateDto> expectedBookings = Arrays.asList();

        when(bookingService.getAllItemBookingToUser(userId, "ALL")).thenReturn(expectedBookings);

        ResponseEntity<List<BookingCreateDto>> response = bookingController.getAllItemBookingToUser(userId, "ALL");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(bookingService, times(1)).getAllItemBookingToUser(userId, "ALL");
    }

    @Test
    void getAllItemBookingToUser_WithDifferentStates_ShouldReturnFilteredBookings() {

        Long userId = 1L;
        String[] states = {"WAITING", "REJECTED", "CURRENT", "PAST", "FUTURE"};

        for (String state : states) {
            List<BookingCreateDto> expectedBookings = Arrays.asList(new BookingCreateDto());
            when(bookingService.getAllItemBookingToUser(userId, state)).thenReturn(expectedBookings);

            ResponseEntity<List<BookingCreateDto>> response = bookingController.getAllItemBookingToUser(userId, state);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().size());
            verify(bookingService, times(1)).getAllItemBookingToUser(userId, state);

            reset(bookingService);
        }
    }
}
