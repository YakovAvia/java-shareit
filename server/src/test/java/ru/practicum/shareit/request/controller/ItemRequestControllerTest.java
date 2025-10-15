package ru.practicum.shareit.request.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestService requestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    @Test
    void createItemRequest_WithValidData_ShouldReturnCreatedRequest() {
        Long userId = 1L;
        CreateItemRequestDto createDto = new CreateItemRequestDto();
        createDto.setDescription("Нужна дрель");

        ItemRequestDto expectedResponse = new ItemRequestDto();
        expectedResponse.setId(1L);
        expectedResponse.setDescription("Нужна дрель");

        when(requestService.createItemRequest(eq(userId), any(CreateItemRequestDto.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<ItemRequestDto> response = itemRequestController.createItemRequest(userId, createDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        verify(requestService, times(1)).createItemRequest(userId, createDto);
    }

    @Test
    void getItemRequests_WhenUserExists_ShouldReturnUserRequests() {

        Long userId = 1L;
        ItemRequestDto request1 = new ItemRequestDto();
        request1.setId(1L);
        request1.setDescription("Нужна дрель");

        ItemRequestDto request2 = new ItemRequestDto();
        request2.setId(2L);
        request2.setDescription("Нужен молоток");

        List<ItemRequestDto> expectedRequests = Arrays.asList(request1, request2);

        when(requestService.getItemRequests(userId)).thenReturn(expectedRequests);

        ResponseEntity<List<ItemRequestDto>> response = itemRequestController.getItemRequests(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(expectedRequests, response.getBody());
        verify(requestService, times(1)).getItemRequests(userId);
    }

    @Test
    void getAllRequests_WithPagination_ShouldReturnOtherUsersRequests() {

        Long userId = 1L;
        int from = 0;
        int size = 10;

        ItemRequestDto request1 = new ItemRequestDto();
        request1.setId(2L);
        request1.setDescription("Запрос от другого пользователя");

        List<ItemRequestDto> expectedRequests = Arrays.asList(request1);

        when(requestService.getAllItemRequests(userId, from, size)).thenReturn(expectedRequests);

        // When
        ResponseEntity<List<ItemRequestDto>> response = itemRequestController.getAllRequests(userId, from, size);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(expectedRequests, response.getBody());
        verify(requestService, times(1)).getAllItemRequests(userId, from, size);
    }

    @Test
    void getAllRequests_WithDefaultPagination_ShouldUseDefaultValues() {

        Long userId = 1L;
        List<ItemRequestDto> expectedRequests = Arrays.asList();

        when(requestService.getAllItemRequests(userId, 0, 10)).thenReturn(expectedRequests);

        ResponseEntity<List<ItemRequestDto>> response = itemRequestController.getAllRequests(userId, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(requestService, times(1)).getAllItemRequests(userId, 0, 10);
    }

    @Test
    void getItemRequest_WhenRequestExists_ShouldReturnRequest() {

        Long userId = 1L;
        Long requestId = 1L;

        ItemRequestDto expectedRequest = new ItemRequestDto();
        expectedRequest.setId(requestId);
        expectedRequest.setDescription("Нужна дрель");

        when(requestService.getItemRequest(userId, requestId)).thenReturn(expectedRequest);

        ResponseEntity<ItemRequestDto> response = itemRequestController.getItemRequest(userId, requestId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedRequest, response.getBody());
        verify(requestService, times(1)).getItemRequest(userId, requestId);
    }
}
