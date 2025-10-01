package ru.practicum.shareit.exception.dto;

import lombok.Data;

@Data
public class ErrorDto {
    private String message;

    public ErrorDto(String message) {
        this.message = message;
    }

}
