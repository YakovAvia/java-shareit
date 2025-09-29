package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private static final String HEADER_REQUEST_ID = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping()
    public ResponseEntity<ItemDto> createItem(@RequestHeader(HEADER_REQUEST_ID) Long userId,
                                              @Valid @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok(itemService.createItem(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(HEADER_REQUEST_ID) Long userId,
                                              @NotNull(message = "itemId не должен быть null") @PathVariable Long itemId,
                                              @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok(itemService.updateItem(userId, itemId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.getItem(itemId));
    }

    @GetMapping()
    public ResponseEntity<List<ItemDto>> getItemUser(@RequestHeader(HEADER_REQUEST_ID) Long userId) {
        return ResponseEntity.ok(itemService.getItemUser(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItem(@RequestHeader(HEADER_REQUEST_ID) Long userId,
                                                    @RequestParam String text) {
        return ResponseEntity.ok(itemService.searchItem(userId, text));
    }

}
