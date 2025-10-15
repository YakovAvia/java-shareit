package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateItemRequestDTO {

    private Long id;
    @NotBlank
    private String description;
    private Long requestorId;
    private LocalDateTime created = LocalDateTime.now();

}