package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.RequestBookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;


/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingService bookingService;

    private static final String HEADER_REQUEST_ID = "X-Sharer-User-Id";

    @PostMapping()
    public ResponseEntity<BookingCreateDto> createBooking(@RequestHeader(HEADER_REQUEST_ID) Long userId, @Valid @RequestBody RequestBookingCreateDto bookingDto) {
        log.info("Создаем заявку для бронирования вещи: {}.", bookingDto);
        return ResponseEntity.ok(bookingService.createBooking(bookingDto, userId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingCreateDto> updateBookingStatus(@RequestHeader(HEADER_REQUEST_ID) Long userId,
                                                          @PathVariable Long bookingId,
                                                          @RequestParam Boolean approved) {
        log.info("Обновляем у бронирования ID: {} статус.", bookingId);
        return ResponseEntity.ok(bookingService.updateBookingStatus(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingCreateDto> getBooking(@RequestHeader(HEADER_REQUEST_ID) Long userId, @PathVariable Long bookingId) {
        log.info("Получение данных о конкретном бронировании (включая его статус).");
        return ResponseEntity.ok(bookingService.getBooking(userId, bookingId));
    }

    @GetMapping()
    public ResponseEntity<List<BookingCreateDto>> getAllBookingsToUser(@RequestHeader(HEADER_REQUEST_ID) Long userId,
                                                                 @RequestParam(required = false, defaultValue = "ALL") String state) {
        log.info("Получение списка всех бронирований текущего пользователя.");
        return ResponseEntity.ok(bookingService.getAllBookingsToUser(userId, state));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingCreateDto>> getAllItemBookingToUser(@RequestHeader(HEADER_REQUEST_ID) Long userId,
                                                                    @RequestParam(required = false,defaultValue = "ALL") String state) {
        log.info("Получение списка бронирований для всех вещей текущего пользователя.");
        return ResponseEntity.ok(bookingService.getAllItemBookingToUser(userId, state));
    }
}
