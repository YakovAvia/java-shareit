package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private static final String HEADER_REQUEST_ID = "X-Sharer-User-Id";

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(HEADER_REQUEST_ID) Long userId,
                                              @Valid @RequestBody ItemDto itemDto) {
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(HEADER_REQUEST_ID) Long userId,
                                              @NotNull(message = "itemId не должен быть null") @PathVariable Long itemId,
                                              @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(HEADER_REQUEST_ID) Long userId,
                                              @PathVariable Long itemId) {
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping()
    public ResponseEntity<Object> getItemUser(@RequestHeader(HEADER_REQUEST_ID) Long userId) {
        return itemClient.getItemUser(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(HEADER_REQUEST_ID) Long userId,
                                                    @RequestParam String text) {
        return itemClient.searchItem(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(HEADER_REQUEST_ID) Long userId,
                                                    @RequestBody CreateCommentDto createCommentDto,
                                                    @PathVariable Long itemId
    ) {
        log.info("Добавление нового комментария для предмета.");
        return itemClient.addComment(userId, createCommentDto, itemId);
    }

}
