package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.RequestBookingCreateDto;
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
    public BookingCreateDto createBooking(RequestBookingCreateDto bookingDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь с id {} не найден", userId);
                    return new NotFoundException("Пользователь не найден");
                });

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> {
                    log.error("Предмет с id {} не найден", bookingDto.getItemId());
                    return new NotFoundException("Предмет не найден");
                });

        if (!item.getAvailable()) {
            log.error("Предмет {} недоступен для бронирования", item.getId());
            throw new ValidationException("Предмет недоступен для бронирования");
        }

        return BookingMapper.toBookingCreateDto(bookingRepository.save(BookingMapper.toBooking(bookingDto, user, item)));
    }

    @Override
    @Transactional
    public BookingCreateDto updateBookingStatus(Long userId, Long bookingId, boolean approved) {
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
        return BookingMapper.toBookingCreateDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingCreateDto getBooking(Long userID, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Заявка на бронирование не найдена!"));
        boolean ownerItemOrBid = ((booking.getItem().getUser().getId().equals(userID) || (booking.getBooker().getId().equals(userID))));
        if (ownerItemOrBid) {
            return BookingMapper.toBookingCreateDto(booking);
        } else {
            throw new ValidationException("Пользователь не автор заявки и не владелец вещи!");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingCreateDto> getAllBookingsToUser(Long userId, String state) {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        return switch (state) {
            case "ALL" -> BookingMapper.toListBookingDto(bookingRepository.findByBooker_Id(userId, sort));
            case "WAITING" ->
                    BookingMapper.toListBookingDto(bookingRepository.findByBooker_IdAndStatus(userId, BookingStatus.WAITING, sort));
            case "REJECTED" ->
                    BookingMapper.toListBookingDto(bookingRepository.findByBooker_IdAndStatus(userId, BookingStatus.REJECTED, sort));
            case "CURRENT" ->
                    BookingMapper.toListBookingDto(bookingRepository.findCurrentByBooker(userId, LocalDateTime.now()));
            case "PAST" ->
                    BookingMapper.toListBookingDto(bookingRepository.findByBooker_IdAndEndBefore(userId, LocalDateTime.now(), sort));
            case "FUTURE" ->
                    BookingMapper.toListBookingDto(bookingRepository.findByBooker_IdAndStartAfter(userId, LocalDateTime.now(), sort));
            default -> throw new ValidationException("Передан не обрабатываемый тип state!");
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingCreateDto> getAllItemBookingToUser(Long userId, String state) {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        if (!itemRepository.findByUserId(userId).isEmpty()) {
            return switch (state) {
                case "ALL" -> BookingMapper.toListBookingDto(bookingRepository.findByItem_User_Id(userId, sort));
                case "WAITING" ->
                        BookingMapper.toListBookingDto(bookingRepository.findByItem_User_IdAndStatus(userId, BookingStatus.WAITING, sort));
                case "REJECTED" ->
                        BookingMapper.toListBookingDto(bookingRepository.findByItem_User_IdAndStatus(userId, BookingStatus.REJECTED, sort));
                case "CURRENT" ->
                        BookingMapper.toListBookingDto(bookingRepository.findByItemUser(userId, LocalDateTime.now()));
                case "PAST" ->
                        BookingMapper.toListBookingDto(bookingRepository.findByItem_User_IdAndEndBefore(userId, LocalDateTime.now(), sort));
                case "FUTURE" ->
                        BookingMapper.toListBookingDto(bookingRepository.findByItem_User_IdAndStartAfter(userId, LocalDateTime.now(), sort));
                default -> throw new ValidationException("Передан не обрабатываемый тип state!");
            };
        } else {
            log.error("У пользователя ID: {} должен быть хоть 1 предмет", userId);
            throw new NotFoundException("У пользователя должен быть хоть 1 предмет");
        }
    }

}
