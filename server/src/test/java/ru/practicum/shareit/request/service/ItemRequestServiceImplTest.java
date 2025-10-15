package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.CreateItemRequestDTO;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.impl.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void createItemRequest_whenUserFound_thenSaveRequest() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);

        CreateItemRequestDTO dto = new CreateItemRequestDTO();
        dto.setDescription("Test request");

        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription(dto.getDescription());
        request.setRequestor(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(request);

        assertNotNull(itemRequestService.createItemRequest(userId, dto));
    }

    @Test
    void createItemRequest_whenUserNotFound_thenThrowNotFoundException() {
        long userId = 1L;
        CreateItemRequestDTO dto = new CreateItemRequestDTO();
        dto.setDescription("Test request");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.createItemRequest(userId, dto));
    }
}
