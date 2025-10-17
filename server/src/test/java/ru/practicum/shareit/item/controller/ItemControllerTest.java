package ru.practicum.shareit.item.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @Test
    void createItem_WithValidData_ShouldReturnCreatedItem() {
        Long userId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Дрель");
        itemDto.setDescription("Аккумуляторная дрель");

        ItemDto createdItem = new ItemDto();
        createdItem.setId(1L);
        createdItem.setName("Дрель");
        createdItem.setDescription("Аккумуляторная дрель");

        when(itemService.createItem(userId, itemDto)).thenReturn(createdItem);

        ResponseEntity<ItemDto> response = itemController.createItem(userId, itemDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdItem, response.getBody());
        verify(itemService, times(1)).createItem(userId, itemDto);
    }

    @Test
    void updateItem_WithValidData_ShouldReturnUpdatedItem() {

        Long userId = 1L;
        Long itemId = 1L;
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Дрель обновленная");

        ItemDto updatedItem = new ItemDto();
        updatedItem.setId(itemId);
        updatedItem.setName("Дрель обновленная");
        updatedItem.setDescription("Аккумуляторная дрель");

        when(itemService.updateItem(userId, itemId, updateDto)).thenReturn(updatedItem);

        ResponseEntity<ItemDto> response = itemController.updateItem(userId, itemId, updateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updatedItem, response.getBody());
        verify(itemService, times(1)).updateItem(userId, itemId, updateDto);
    }

    @Test
    void getItem_WhenItemExists_ShouldReturnItem() {

        Long userId = 1L;
        Long itemId = 1L;

        GetItemDto expectedItem = new GetItemDto();
        expectedItem.setId(itemId);
        expectedItem.setName("Дрель");
        expectedItem.setDescription("Аккумуляторная дрель");

        when(itemService.getItem(itemId, userId)).thenReturn(expectedItem);

        ResponseEntity<GetItemDto> response = itemController.getItem(userId, itemId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedItem, response.getBody());
        verify(itemService, times(1)).getItem(itemId, userId);
    }

    @Test
    void getItemUser_WhenUserExists_ShouldReturnUserItems() {

        Long userId = 1L;

        ItemDto item1 = new ItemDto();
        item1.setId(1L);
        item1.setName("Дрель");

        ItemDto item2 = new ItemDto();
        item2.setId(2L);
        item2.setName("Молоток");

        List<ItemDto> expectedItems = Arrays.asList(item1, item2);

        when(itemService.getItemUser(userId)).thenReturn(expectedItems);

        ResponseEntity<List<ItemDto>> response = itemController.getItemUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(expectedItems, response.getBody());
        verify(itemService, times(1)).getItemUser(userId);
    }

    @Test
    void searchItem_WithValidText_ShouldReturnFoundItems() {

        Long userId = 1L;
        String searchText = "дрель";

        ItemDto item1 = new ItemDto();
        item1.setId(1L);
        item1.setName("Дрель");
        item1.setDescription("Аккумуляторная дрель");

        List<ItemDto> expectedItems = Arrays.asList(item1);

        when(itemService.searchItem(userId, searchText)).thenReturn(expectedItems);

        ResponseEntity<List<ItemDto>> response = itemController.searchItem(userId, searchText);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(expectedItems, response.getBody());
        verify(itemService, times(1)).searchItem(userId, searchText);
    }

    @Test
    void searchItem_WithEmptyText_ShouldReturnEmptyList() {

        Long userId = 1L;
        String searchText = "";

        when(itemService.searchItem(userId, searchText)).thenReturn(Arrays.asList());

        ResponseEntity<List<ItemDto>> response = itemController.searchItem(userId, searchText);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(itemService, times(1)).searchItem(userId, searchText);
    }

    @Test
    void createComment_WithValidData_ShouldReturnCreatedComment() {
        Long userId = 1L;
        Long itemId = 1L;
        CreateCommentDto createCommentDto = new CreateCommentDto();
        createCommentDto.setText("Отличная дрель!");

        CommentDto createdComment = new CommentDto();
        createdComment.setId(1L);
        createdComment.setText("Отличная дрель!");

        when(itemService.addComment(userId, createCommentDto, itemId)).thenReturn(createdComment);

        ResponseEntity<CommentDto> response = itemController.createComment(userId, createCommentDto, itemId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdComment, response.getBody());
        verify(itemService, times(1)).addComment(userId, createCommentDto, itemId);
    }
}