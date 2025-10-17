package ru.practicum.shareit.item.dto.mappers;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public final class ItemMappers {

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);

        return itemDto;
    }

    public static ItemDto toUpdateItemDto(Item item, Booking lastBooking, Booking nextBooking) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);

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

        return itemDto;
    }

    public static Item toItem(ItemDto itemDto, User user) {

        Item newItem = new Item();
        newItem.setName(itemDto.getName());
        newItem.setDescription(itemDto.getDescription());
        newItem.setAvailable(itemDto.getAvailable());

        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = new ItemRequest();
            itemRequest.setId(itemDto.getRequestId());
            newItem.setRequest(itemRequest);
        }

        newItem.setUser(user);
        return newItem;
    }

    public static GetItemDto toItemAndCommentDto(Item item, List<Comment> comment, Booking lastBooking, Booking nextBooking) {
        GetItemDto itemDto = new GetItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
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
                .map(CommentMappers::toCommentDto)
                .collect(Collectors.toList()));
        return itemDto;
    }

}
