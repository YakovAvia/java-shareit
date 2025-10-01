package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mappers.CommentMappers;
import ru.practicum.shareit.item.dto.mappers.ItemMappers;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = userRepository.findUserById(userId);

        if (user == null) {
            log.error("User c id {} не найден!", userId);
            throw new NotFoundException("Пользователь не найден!");
        }

        return ItemMappers.toItemDto(itemRepository.save(ItemMappers.toItem(itemDto, user)));
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        User user = userRepository.findUserById(userId);
        Item updateItem = itemRepository.findItemById(itemId);

        if (user == null) {
            log.error("Пользователь с id: {}, не найден!", userId);
            throw new NotFoundException("Пользователь не найден!");
        }

        if (!updateItem.getUser().getId().equals(user.getId())) {
            log.error("Пользователь с id: {}, не может менять параметры предмета!", user.getId());
            throw new ValidationException("Пользователь не может менять параметры предмета!");
        }

        if (itemDto.getName() != null) {
            updateItem.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            updateItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            updateItem.setAvailable(itemDto.getAvailable());
        }

        return ItemMappers.toItemDto(itemRepository.save(updateItem));
    }

    @Override
    public GetItemDto getItem(Long itemId, Long userId) {
        Item item = itemRepository.findItemById(itemId);
        if (item == null) {
            log.warn("Пользователь запросил item id: {}, но такого предмета не существует!", itemId);
            throw new NotFoundException("Item не существует!");
        }
        Booking bookingLast;
        Booking bookingNext;
        if (item.getUser().getId().equals(userId)) {
            bookingLast = bookingRepository.findLastBooking(itemId, LocalDateTime.now());
            bookingNext = bookingRepository.findNextBooking(itemId, LocalDateTime.now());
        } else {
            bookingLast = null;
            bookingNext = null;
        }

        return ItemMappers.toItemAndCommentDto(item, commentRepository.findByItemId(itemId), bookingLast, bookingNext);
    }

    @Override
    public List<ItemDto> getItemUser(Long userId) {
        log.info("Получаем предметы для User - {}", userId);
        return itemRepository.findByUserId(userId).stream().map(ItemMappers::toItemDto).toList();
    }

    @Override
    public List<ItemDto> searchItem(Long userId, String text) {
        List<Item> itemList = List.of();
        if (text == null || text.isEmpty()) {
            log.info("Ничего не было указано!");
            return itemList.stream().map(ItemMappers::toItemDto).toList();
        }
        log.info("Возвращаем список с фильтром: {}", text);
        return itemRepository.searchItem(text).stream().map(ItemMappers::toItemDto).toList();
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, CreateCommentDto createCommentDto, Long itemId) {
        User user = userRepository.findUserById(userId);
        List<Booking> bookings = bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBefore(
                userId,
                itemId,
                BookingStatus.APPROVED,
                LocalDateTime.now()
        );
        if (bookings.isEmpty()) {
            log.error("Пользователь не арендовал эту вещь или бронирование еще не завершено!");
            throw new ValidationException("Пользователь не арендовал эту вещь или бронирование еще не завершено!");
        }
        return CommentMappers.toCommentDto(commentRepository.save(CommentMappers.toComment(createCommentDto, user, itemRepository.findItemById(itemId))));
    }

}
