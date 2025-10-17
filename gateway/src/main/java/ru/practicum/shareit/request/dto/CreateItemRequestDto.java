package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateItemRequestDto {
    @NotBlank
    private String description;
    private Long requestorId;
    private LocalDateTime created = LocalDateTime.now();

}