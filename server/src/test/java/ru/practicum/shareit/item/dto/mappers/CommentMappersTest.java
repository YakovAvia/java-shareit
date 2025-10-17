package ru.practicum.shareit.item.dto.mappers;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CommentMappersTest {

    @Test
    void toComment() {
        CreateCommentDto createCommentDto = new CreateCommentDto();
        createCommentDto.setText("Test comment");

        User user = new User();
        user.setId(1L);
        user.setName("Test User");

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");

        Comment comment = CommentMappers.toComment(createCommentDto, user, item);

        assertNotNull(comment);
        assertEquals(createCommentDto.getText(), comment.getText());
        assertEquals(user, comment.getAuthor());
        assertEquals(item, comment.getItem());
        assertNotNull(comment.getCreated());
    }

    @Test
    void toCommentDto() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");
        comment.setCreated(LocalDateTime.now());

        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        comment.setAuthor(user);

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        comment.setItem(item);

        CommentDto commentDto = CommentMappers.toCommentDto(comment);

        assertNotNull(commentDto);
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getCreated(), commentDto.getCreated());
        assertEquals(user.getName(), commentDto.getAuthorName());
        assertNotNull(commentDto.getItem());
        assertEquals(item.getId(), commentDto.getItem().getId());
    }
}
