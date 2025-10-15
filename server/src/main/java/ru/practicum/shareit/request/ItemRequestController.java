package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDTO;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService requestService;

    private static final String HEADER_REQUEST_ID = "X-Sharer-User-Id";

    @PostMapping()
    public ResponseEntity<ItemRequestDto> createItemRequest(@RequestHeader(HEADER_REQUEST_ID) Long userId, @RequestBody CreateItemRequestDTO dto) {
        log.info("Создаем запрос на поиск вещи");
        return ResponseEntity.ok(requestService.createItemRequest(userId, dto));
    }

    @GetMapping()
    public ResponseEntity<List<ItemRequestDto>> getItemRequests(@RequestHeader(HEADER_REQUEST_ID) Long userId) {
        log.info("Получаем полный список запросов данного пользователя");
        return ResponseEntity.ok(requestService.getItemRequests(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllRequests(@RequestHeader(HEADER_REQUEST_ID) Long userId,
                                                               @RequestParam(defaultValue = "0") int from,
                                                               @RequestParam(defaultValue = "10") int size) {
        log.info("Получаем полный список заявок");
        return ResponseEntity.ok(requestService.getAllItemRequests(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getItemRequest(@RequestHeader(HEADER_REQUEST_ID) Long userId, @PathVariable Long requestId) {
        log.info("Получаем только одну заявку");
        return ResponseEntity.ok(requestService.getItemRequest(userId, requestId));
    }
}
