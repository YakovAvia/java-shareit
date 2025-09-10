package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto createItem(Long userId, Item item);

    ItemDto updateItem(Long userId, Long itemId, Item item);

    ItemDto getItem(Long itemId);

    List<ItemDto> getItemUser(Long userId);

    List<ItemDto> searchItem(Long userId, String text);

}
