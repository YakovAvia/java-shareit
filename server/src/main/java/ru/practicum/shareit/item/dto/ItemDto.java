package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;

/**
 * TODO Sprint add-controllers.
 */

@Data
public class ItemDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long user;

    private Long requestId;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

}
