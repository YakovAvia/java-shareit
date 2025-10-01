package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final HashMap<Long, Item> items = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> getItemUser(Long userId) {
        return items.values().stream()
                .filter(i -> i.getUser().getId().equals(userId))
                .toList();
    }

    @Override
    public List<Item> searchItem(String text) {
        return items.values().stream()
                .filter(i -> i.getName() != null && i.getName().toLowerCase().contains(text.toLowerCase()) ||
                        i.getDescription() != null && i.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .toList();
    }

    private Long generateId() {
        long itemId = items.values().stream()
                .mapToLong(Item::getId)
                .max()
                .orElse(0L);
        return itemId + 1;
    }

    @Override
    public Item findItemById(Long itemId) {
        return items.getOrDefault(itemId, null);
    }

}
