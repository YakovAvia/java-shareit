package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDTO;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@Data
@Slf4j
public class ItemRequestController {
    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    private static final String HEADER_REQUEST_ID = "X-Sharer-User-Id";

    private final ItemRequestClient itemRequestClient;

    @PostMapping()
    public ResponseEntity<Object> createItemRequest(@RequestHeader(HEADER_REQUEST_ID) Long userId, @Valid @RequestBody CreateItemRequestDTO dto) {
        log.info("Создаем запрос на поиск вещи");
        return itemRequestClient.createItemRequest(userId, dto);
    }

    @GetMapping()
    public ResponseEntity<Object> getItemRequests(@RequestHeader(HEADER_REQUEST_ID) Long userId) {
        log.info("Получаем полный список запросов данного пользователя");
        return itemRequestClient.getItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(HEADER_REQUEST_ID) long userId, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "10") int size) {
        log.info("Получаем полный список заявок");
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader(HEADER_REQUEST_ID) long userId, @PathVariable long requestId) {
        log.info("Получаем только одну заявку");
        return itemRequestClient.getItemRequest(userId, requestId);
    }
}
