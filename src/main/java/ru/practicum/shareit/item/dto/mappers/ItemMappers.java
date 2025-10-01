package ru.practicum.shareit.item.dto.mappers;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public final class ItemMappers {

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequest(item.getRequest() != null ? item.getRequest().getId() : null);
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto, User user) {
        Item newItem = new Item();
        newItem.setName(itemDto.getName());
        newItem.setDescription(itemDto.getDescription());
        newItem.setAvailable(itemDto.getAvailable() != null ? itemDto.getAvailable() : false);
        newItem.setUser(user);
        return newItem;
    }

    public static ItemDto toItemAndCommentDto(Item item, List<Comment> comment, Booking lastBooking, Booking nextBooking) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequest(item.getRequest() != null ? item.getRequest().getId() : null);
        if (lastBooking != null) {
            BookingDto bookingDto = new BookingDto();
            bookingDto.setId(lastBooking.getId());
            bookingDto.setStart(lastBooking.getStart());
            itemDto.setLastBooking(bookingDto);
        } else {
            itemDto.setLastBooking(null);
        }
        if (nextBooking != null) {
            BookingDto bookingDto = new BookingDto();
            bookingDto.setId(nextBooking.getId());
            bookingDto.setStart(nextBooking.getStart());
            itemDto.setNextBooking(bookingDto);
        } else {
            itemDto.setNextBooking(null);
        }

        itemDto.setComments(comment.stream()
                .map(comment1 -> {
                    Comment comment2 = new Comment();
                    comment2.setId(comment1.getId());
                    comment2.setText(comment1.getText());
                    return comment2;
                }).toList());
        return itemDto;
    }

}
