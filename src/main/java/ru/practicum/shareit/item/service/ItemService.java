package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;


public interface ItemService {

    ItemDto createItem(Long userId, ItemDto item);

    ItemDto updateItem(Long userId, Long itemId, ItemDto item);

    ItemDto getItem(Long itemId);

    List<ItemDto> getItemUser(Long userId);

    List<ItemDto> searchItem(Long userId, String text);

    CommentDto addComment(Long userId, CreateCommentDto createCommentDto, Long itemId);

}
