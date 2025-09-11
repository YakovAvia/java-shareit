package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mappers.ItemMappers;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = userRepository.getUser(userId);

        if (user == null) {
            log.error("User c id {} не найден!", userId);
            throw new NotFoundException("Пользователь не найден!");
        }

        Item item = itemRepository.createItem(ItemMappers.toItem(itemDto, user));
        return ItemMappers.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        User user = userRepository.getUser(userId);
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

        return ItemMappers.toItemDto(itemRepository.updateItem(updateItem));
    }

    @Override
    public ItemDto getItem(Long itemId) {
        Item item = itemRepository.findItemById(itemId);
        if (item == null) {
            log.warn("Пользователь запросил item id: {}, но такого предмета не существует!", itemId);
            throw new NotFoundException("Item не существует!");
        }
        return ItemMappers.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemUser(Long userId) {
        log.info("Получаем предметы для User - {}", userId);
        return itemRepository.getItemUser(userId).stream().map(ItemMappers::toItemDto).toList();
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

}
