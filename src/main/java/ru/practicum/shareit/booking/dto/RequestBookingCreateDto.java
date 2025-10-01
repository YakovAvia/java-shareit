package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestBookingCreateDto {

    private LocalDateTime start;

    private LocalDateTime end;

    @NotNull
    private Long itemId;

}
