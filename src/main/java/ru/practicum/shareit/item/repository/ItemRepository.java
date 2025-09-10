package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository {

    Item createItem(User user, Item item);

    Item updateItem(Long userId, Long itemId, Item item);

    List<Item> getItemUser(Long userId);

    List<Item> searchItem(String text);

    Item findItemById(Long id);

}
