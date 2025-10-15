package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
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
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void createItem_WithValidData_ShouldCreateItem() {
        // Given
        Long userId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Дрель");
        itemDto.setDescription("Аккумуляторная дрель");
        itemDto.setAvailable(true);

        User user = new User();
        user.setId(userId);
        user.setName("John Doe");

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Аккумуляторная дрель");
        item.setAvailable(true);
        item.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.createItem(userId, itemDto);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void createItem_WhenUserNotFound_ShouldThrowException() {

        Long userId = 999L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Дрель");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(userId, itemDto));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItem_WithValidData_ShouldUpdateItem() {

        Long userId = 1L;
        Long itemId = 1L;
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Дрель обновленная");
        updateDto.setDescription("Новое описание");

        User user = new User();
        user.setId(userId);

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setName("Дрель");
        existingItem.setDescription("Старое описание");
        existingItem.setAvailable(true);
        existingItem.setUser(user);

        Item updatedItem = new Item();
        updatedItem.setId(itemId);
        updatedItem.setName("Дрель обновленная");
        updatedItem.setDescription("Новое описание");
        updatedItem.setAvailable(true);
        updatedItem.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        ItemDto result = itemService.updateItem(userId, itemId, updateDto);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, times(1)).save(existingItem);
    }

    @Test
    void updateItem_WhenUserNotOwner_ShouldThrowException() {

        Long userId = 1L;
        Long itemId = 1L;
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Дрель обновленная");

        User owner = new User();
        owner.setId(2L);

        User currentUser = new User();
        currentUser.setId(userId);

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setUser(owner);

        when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        assertThrows(ValidationException.class, () -> itemService.updateItem(userId, itemId, updateDto));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItem_WhenUserNotFound_ShouldThrowException() {
        // Given
        Long userId = 999L;
        Long itemId = 1L;
        ItemDto updateDto = new ItemDto();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(userId, itemId, updateDto));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, never()).findById(anyLong());
    }

    @Test
    void updateItem_WhenItemNotFound_ShouldThrowException() {

        Long userId = 1L;
        Long itemId = 999L;
        ItemDto updateDto = new ItemDto();

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(userId, itemId, updateDto));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, never()).save(any(Item.class));
    }



    @Test
    void getItem_WhenItemExistsAndUserIsNotOwner_ShouldReturnItemWithoutBookings() {

        Long userId = 2L;
        Long itemId = 1L;

        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(itemId);
        item.setName("Дрель");
        item.setUser(owner);

        List<Comment> comments = Arrays.asList();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(itemId)).thenReturn(comments);

        GetItemDto result = itemService.getItem(itemId, userId);

        assertNotNull(result);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, never()).findLastBooking(anyLong(), any(LocalDateTime.class));
        verify(bookingRepository, never()).findNextBooking(anyLong(), any(LocalDateTime.class));
        verify(commentRepository, times(1)).findByItemId(itemId);
    }

    @Test
    void getItem_WhenItemNotFound_ShouldThrowException() {

        Long userId = 1L;
        Long itemId = 999L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItem(itemId, userId));
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, never()).findLastBooking(anyLong(), any(LocalDateTime.class));
        verify(commentRepository, never()).findByItemId(anyLong());
    }

    @Test
    void getItemUser_WhenUserExists_ShouldReturnUserItems() {

        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Дрель");
        item1.setUser(user);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Молоток");
        item2.setUser(user);

        List<Item> userItems = Arrays.asList(item1, item2);

        when(itemRepository.findAllByUser_Id(userId)).thenReturn(userItems);

        List<ItemDto> result = itemService.getItemUser(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(itemRepository, times(1)).findAllByUser_Id(userId);
    }

    @Test
    void searchItem_WithValidText_ShouldReturnFoundItems() {

        Long userId = 1L;
        String searchText = "дрель";

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Дрель");
        item1.setAvailable(true);

        List<Item> foundItems = Arrays.asList(item1);

        when(itemRepository.searchItem(searchText)).thenReturn(foundItems);

        List<ItemDto> result = itemService.searchItem(userId, searchText);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(itemRepository, times(1)).searchItem(searchText);
    }

    @Test
    void searchItem_WithEmptyText_ShouldReturnEmptyList() {

        Long userId = 1L;
        String searchText = "";

        List<ItemDto> result = itemService.searchItem(userId, searchText);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(itemRepository, never()).searchItem(anyString());
    }

    @Test
    void searchItem_WithNullText_ShouldReturnEmptyList() {

        Long userId = 1L;
        String searchText = null;

        List<ItemDto> result = itemService.searchItem(userId, searchText);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(itemRepository, never()).searchItem(anyString());
    }

    @Test
    void addComment_WithValidData_ShouldCreateComment() {

        Long userId = 1L;
        Long itemId = 1L;
        CreateCommentDto createCommentDto = new CreateCommentDto();
        createCommentDto.setText("Отличная дрель!");

        User user = new User();
        user.setId(userId);

        Item item = new Item();
        item.setId(itemId);

        Booking booking = new Booking();
        booking.setId(1L);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Отличная дрель!");
        comment.setAuthor(user);
        comment.setItem(item);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBefore(
                eq(userId), eq(itemId), eq(BookingStatus.APPROVED), any(LocalDateTime.class))
        ).thenReturn(Arrays.asList(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.addComment(userId, createCommentDto, itemId);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, times(1)).findByBookerIdAndItemIdAndStatusAndEndBefore(
                eq(userId), eq(itemId), eq(BookingStatus.APPROVED), any(LocalDateTime.class));
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void addComment_WhenUserNotFound_ShouldThrowException() {

        Long userId = 999L;
        Long itemId = 1L;
        CreateCommentDto createCommentDto = new CreateCommentDto();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addComment(userId, createCommentDto, itemId));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, never()).findById(anyLong());
        verify(commentRepository, never()).save(any(Comment.class));
    }



    @Test
    void getRequestItem_WhenRequestExists_ShouldReturnItems() {

        Long requestId = 1L;

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Дрель");

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Молоток");

        List<Item> requestItems = Arrays.asList(item1, item2);

        when(itemRepository.findAllByRequest_Id(requestId)).thenReturn(requestItems);

        List<Item> result = itemService.getRequestItem(requestId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(itemRepository, times(1)).findAllByRequest_Id(requestId);
    }
}
