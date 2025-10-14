package ru.practicum.shareit.request.dto.mapper;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetItem {

    private String description;
    private LocalDateTime create;



}
