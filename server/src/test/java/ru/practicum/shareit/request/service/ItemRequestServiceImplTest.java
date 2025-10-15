package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.CreateItemRequestDTO;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.impl.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void createItemRequest_WithValidData_ShouldCreateRequest() {

        Long userId = 1L;
        CreateItemRequestDTO createDto = new CreateItemRequestDTO();
        createDto.setDescription("Нужна дрель");

        User user = new User();
        user.setId(userId);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        ItemRequest savedRequest = new ItemRequest();
        savedRequest.setId(1L);
        savedRequest.setDescription("Нужна дрель");
        savedRequest.setRequestor(user);
        savedRequest.setCreated(LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(savedRequest);

        ItemRequestDto result = itemRequestService.createItemRequest(userId, createDto);

        assertNotNull(result);
        assertEquals(savedRequest.getId(), result.getId());
        assertEquals(savedRequest.getDescription(), result.getDescription());
        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void createItemRequest_WhenUserNotFound_ShouldThrowException() {

        Long userId = 999L;
        CreateItemRequestDTO createDto = new CreateItemRequestDTO();
        createDto.setDescription("Нужна дрель");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.createItemRequest(userId, createDto));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void getItemRequests_WhenUserExists_ShouldReturnUserRequests() {

        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);
        request1.setDescription("Нужна дрель");
        request1.setRequestor(user);

        ItemRequest request2 = new ItemRequest();
        request2.setId(2L);
        request2.setDescription("Нужен молоток");
        request2.setRequestor(user);

        List<ItemRequest> userRequests = Arrays.asList(request1, request2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestor_Id(userId)).thenReturn(userRequests);
        when(itemRepository.findAllByRequest_IdIn(anyList())).thenReturn(Arrays.asList());

        List<ItemRequestDto> result = itemRequestService.getItemRequests(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).findAllByRequestor_Id(userId);
        verify(itemRepository, times(1)).findAllByRequest_IdIn(Arrays.asList(1L, 2L));
    }

    @Test
    void getItemRequests_WhenUserNotFound_ShouldThrowException() {

        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequests(userId));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, never()).findAllByRequestor_Id(anyLong());
    }

    @Test
    void getAllItemRequests_WithPagination_ShouldReturnOtherUsersRequests() {
        Long userId = 1L;
        int from = 0;
        int size = 10;

        User currentUser = new User();
        currentUser.setId(userId);

        User otherUser = new User();
        otherUser.setId(2L);

        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);
        request1.setDescription("Запрос от другого пользователя");
        request1.setRequestor(otherUser);

        List<ItemRequest> otherRequests = Arrays.asList(request1);
        PageRequest pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        Page<ItemRequest> page = new PageImpl<>(otherRequests);

        when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));
        when(itemRequestRepository.findAllByRequestor_IdNot(eq(userId), any(PageRequest.class))).thenReturn(page);
        when(itemRepository.findAllByRequest_IdIn(anyList())).thenReturn(Arrays.asList());

        List<ItemRequestDto> result = itemRequestService.getAllItemRequests(userId, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).findAllByRequestor_IdNot(eq(userId), eq(pageable));
        verify(itemRepository, times(1)).findAllByRequest_IdIn(Arrays.asList(1L));
    }

    @Test
    void getAllItemRequests_WhenUserNotFound_ShouldThrowException() {

        Long userId = 999L;
        int from = 0;
        int size = 10;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getAllItemRequests(userId, from, size));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, never()).findAllByRequestor_IdNot(anyLong(), any(PageRequest.class));
    }

    @Test
    void getItemRequest_WhenRequestExists_ShouldReturnRequest() {

        Long userId = 1L;
        Long requestId = 1L;

        User user = new User();
        user.setId(userId);

        ItemRequest request = new ItemRequest();
        request.setId(requestId);
        request.setDescription("Нужна дрель");
        request.setRequestor(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequest_IdIn(anyList())).thenReturn(Arrays.asList());

        ItemRequestDto result = itemRequestService.getItemRequest(userId, requestId);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
        assertEquals(request.getDescription(), result.getDescription());
        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).findById(requestId);
        verify(itemRepository, times(1)).findAllByRequest_IdIn(Arrays.asList(requestId));
    }

    @Test
    void getItemRequest_WhenUserNotFound_ShouldThrowException() {
        Long userId = 999L;
        Long requestId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequest(userId, requestId));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, never()).findById(anyLong());
    }

    @Test
    void getItemRequest_WhenRequestNotFound_ShouldThrowException() {

        Long userId = 1L;
        Long requestId = 999L;

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequest(userId, requestId));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).findById(requestId);
        verify(itemRepository, never()).findAllByRequest_IdIn(anyList());
    }

    @Test
    void enrichRequestsWithItems_ShouldAddItemsToRequests() {

        User user = new User();
        user.setId(1L);

        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);
        request1.setDescription("Нужна дрель");
        request1.setRequestor(user);

        ItemRequest request2 = new ItemRequest();
        request2.setId(2L);
        request2.setDescription("Нужен молоток");
        request2.setRequestor(user);

        List<ItemRequest> requests = Arrays.asList(request1, request2);

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Дрель");
        item1.setRequest(request1);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Молоток");
        item2.setRequest(request2);

        List<Item> items = Arrays.asList(item1, item2);

        when(itemRepository.findAllByRequest_IdIn(Arrays.asList(1L, 2L))).thenReturn(items);

        List<ItemRequestDto> result = itemRequestService.enrichRequestsWithItems(requests);

        assertNotNull(result);
        assertEquals(2, result.size());

        ItemRequestDto dto1 = result.get(0);
        assertEquals(1L, dto1.getId());
        assertEquals(1, dto1.getItems().size());
        assertEquals("Дрель", dto1.getItems().get(0).getName());

        ItemRequestDto dto2 = result.get(1);
        assertEquals(2L, dto2.getId());
        assertEquals(1, dto2.getItems().size());
        assertEquals("Молоток", dto2.getItems().get(0).getName());

        verify(itemRepository, times(1)).findAllByRequest_IdIn(Arrays.asList(1L, 2L));
    }

    @Test
    void enrichRequestsWithItems_WithEmptyRequests_ShouldReturnEmptyList() {

        List<ItemRequest> requests = Arrays.asList();

        List<ItemRequestDto> result = itemRequestService.enrichRequestsWithItems(requests);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(itemRepository, never()).findAllByRequest_IdIn(anyList());
    }
}
