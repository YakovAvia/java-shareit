package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final List<Item> items = new ArrayList<>();

    @Override
    public Item createItem(User user, Item item) {
        Item newItem = new Item();
        newItem.setId(generateId());
        newItem.setName(item.getName());
        newItem.setDescription(item.getDescription());
        newItem.setAvailable(item.getAvailable() != null ? item.getAvailable() : false);
        newItem.setUser(user);
        newItem.setRequest(item.getRequest());
        items.add(newItem);
        return newItem;
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        Item updatedItem = findItemById(itemId);
        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        return updatedItem;
    }

    @Override
    public List<Item> getItemUser(Long userId) {
        return items.stream()
                .filter(i -> i.getUser().getId().equals(userId))
                .toList();
    }

    @Override
    public List<Item> searchItem(String text) {
        return items.stream()
                .filter(i -> i.getName() != null && i.getName().toLowerCase().contains(text.toLowerCase()) ||
                        i.getDescription() != null && i.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .toList();
    }

    private Long generateId() {
        long itemId = items.stream()
                .mapToLong(Item::getId)
                .max()
                .orElse(0L);
        return itemId + 1;
    }

    @Override
    public Item findItemById(Long itemId) {
        return items.stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElse(null);
    }

}
