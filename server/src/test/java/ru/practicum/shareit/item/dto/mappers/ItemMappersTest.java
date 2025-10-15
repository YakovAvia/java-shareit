package ru.practicum.shareit.item.dto.mappers;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemMappersTest {

    @Test
    void toItemDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        item.setRequest(itemRequest);

        ItemDto itemDto = ItemMappers.toItemDto(item);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(item.getRequest().getId(), itemDto.getRequestId());
    }

    @Test
    void toItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        User user = new User();
        user.setId(1L);

        Item item = ItemMappers.toItem(itemDto, user);

        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
        assertEquals(user, item.getUser());
        assertNotNull(item.getRequest());
        assertEquals(itemDto.getRequestId(), item.getRequest().getId());
    }

    @Test
    void toItemAndCommentDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);

        User user = new User();
        user.setId(1L);
        user.setName("Test User");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test Comment");
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        Booking lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setStart(LocalDateTime.now().minusDays(1));

        Booking nextBooking = new Booking();
        nextBooking.setId(2L);
        nextBooking.setStart(LocalDateTime.now().plusDays(1));

        GetItemDto getItemDto = ItemMappers.toItemAndCommentDto(item, List.of(comment), lastBooking, nextBooking);

        assertEquals(item.getId(), getItemDto.getId());
        assertEquals(item.getName(), getItemDto.getName());
        assertEquals(item.getDescription(), getItemDto.getDescription());
        assertEquals(item.getAvailable(), getItemDto.getAvailable());
        assertNotNull(getItemDto.getLastBooking());
        assertEquals(lastBooking.getId(), getItemDto.getLastBooking().getId());
        assertNotNull(getItemDto.getNextBooking());
        assertEquals(nextBooking.getId(), getItemDto.getNextBooking().getId());
        assertNotNull(getItemDto.getComments());
        assertEquals(1, getItemDto.getComments().size());
        assertEquals(comment.getText(), getItemDto.getComments().get(0).getText());
    }

    @Test
    void toItemAndCommentDto_withNullBookings() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);

        GetItemDto getItemDto = ItemMappers.toItemAndCommentDto(item, Collections.emptyList(), null, null);

        assertEquals(item.getId(), getItemDto.getId());
        assertNull(getItemDto.getLastBooking());
        assertNull(getItemDto.getNextBooking());
        assertTrue(getItemDto.getComments().isEmpty());
    }
}
