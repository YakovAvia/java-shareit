package ru.practicum.shareit.request.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        if (itemRequest == null) {
            return null;
        }

        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setCreated(itemRequest.getCreated());
        if (itemRequest.getRequestor() != null) {
            dto.setRequestorId(itemRequest.getRequestor().getId());
        }
        // The 'items' field will be set in the service layer
        dto.setItems(Collections.emptyList());

        return dto;
    }

    public static List<ItemRequestDto> toDto(List<ItemRequest> itemRequests) {
        if (itemRequests == null) {
            return Collections.emptyList();
        }
        return itemRequests.stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
    }
}