package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private static final String HEADER_REQUEST_ID = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping()
    public ResponseEntity<ItemDto> createItem(@RequestHeader(HEADER_REQUEST_ID) Long userId,
                                             @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok(itemService.createItem(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(HEADER_REQUEST_ID) Long userId,
                                              @PathVariable Long itemId,
                                              @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok(itemService.updateItem(userId, itemId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<GetItemDto> getItem(@RequestHeader(HEADER_REQUEST_ID) Long userId,
                                              @PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.getItem(itemId, userId));
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

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> createComment(@RequestHeader(HEADER_REQUEST_ID) Long userId,
                                                    @RequestBody CreateCommentDto createCommentDto,
                                                    @PathVariable Long itemId
    ) {
        log.info("Добавление нового комментария для предмета.");
        return ResponseEntity.ok(itemService.addComment(userId, createCommentDto, itemId));
    }

}
