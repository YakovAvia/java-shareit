package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
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
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto createBooking(BookingCreateDto bookingDto) {
        User user = userRepository.findUserById(bookingDto.getBroker());
        if (user == null) {
            log.error("При бронирование указа не верный пользователь");
            throw new NotFoundException("При бронирование указа не верный пользователь");
        }
        Item item = itemRepository.findItemById(bookingDto.getItem());
        if (item == null) {
            log.error("При бронировании указан не верный предмет");
            throw new NotFoundException("При бронировании указан не верный предмет");
        }

        return BookingMapper.toBookingDto(bookingRepository.save(BookingMapper.toBooking(bookingDto, user, item)));
    }

    @Override
    public BookingDto updateBookingStatus(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Заявка на бронирование не найдена"));
        boolean ownerItem = booking.getItem().getUser().getId().equals(userId);
        if (ownerItem) {
            if (approved) {
                log.info("Перевели статус заявки бронирования с ID: {} на одобрено", bookingId);
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                log.info("Перевели статус заявки бронирования с ID: {} на отклоненный", bookingId);
                booking.setStatus(BookingStatus.REJECTED);
            }
        } else {
            log.error("Пользователь ID: {} не является владельцем предмета!", userId);
            throw new ValidationException("Пользователь не является владельцем предмета!");
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(Long userID, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Заявка на бронирование не найдена!"));
        boolean ownerItemOrBid = ((booking.getItem().getUser().getId().equals(userID) || (booking.getBooker().getId().equals(userID))));
        if (ownerItemOrBid) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new ValidationException("Пользователь не автор заявки и не владелец вещи!");
        }
    }

    @Override
    public List<BookingDto> getAllBookingsToUser(Long userId, String state) {
        return switch (state) {
            case "ALL" ->
                    BookingMapper.toListBookingDto(bookingRepository.findByBooker_IdOrderByStartDesc(userId));
            case "WAITING" ->
                    BookingMapper.toListBookingDto(bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING));
            case "REJECTED" ->
                    BookingMapper.toListBookingDto(bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED));
            case "CURRENT" ->
                    BookingMapper.toListBookingDto(bookingRepository.findCurrentByBooker(userId, LocalDateTime.now()));
            case "PAST" ->
                    BookingMapper.toListBookingDto(bookingRepository.findByBooker_IdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now()));
            case "FUTURE" ->
                    BookingMapper.toListBookingDto(bookingRepository.findByBooker_IdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now()));
            default ->
                    throw new ValidationException("Передан не обрабатываемый тип");
        };
    }

}
