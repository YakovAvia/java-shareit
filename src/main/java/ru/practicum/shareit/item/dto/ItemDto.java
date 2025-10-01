package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Data
public class ItemDto {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Boolean available;

    private Long user;

    private Long request;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private List<Comment> comments;

}
