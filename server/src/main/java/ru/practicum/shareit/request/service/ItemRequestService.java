package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(Long userId, CreateItemRequestDto dto);

    List<ItemRequestDto> getItemRequests(Long userId);

    List<ItemRequestDto> getAllItemRequests(Long userId, int from, int size);

    ItemRequestDto getItemRequest(Long userId, Long requestId);
}
