package ru.practicum.shareit.item.dto.mappers;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public final class CommentMappers {

    public static Comment toComment(CreateCommentDto createCommentDto, User user, Item item) {
        Comment comment = new Comment();
        comment.setText(createCommentDto.getText());
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(user);
        comment.setItem(item);
        return comment;
    }

    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setCreated(comment.getCreated());
        commentDto.setAuthorName(comment.getAuthor().getName());

        Item item = new Item();
        item.setId(comment.getItem().getId());
        item.setName(comment.getItem().getName());
        commentDto.setItem(item);

        return commentDto;
    }
}
