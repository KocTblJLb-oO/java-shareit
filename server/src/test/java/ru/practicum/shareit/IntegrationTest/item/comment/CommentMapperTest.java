package ru.practicum.shareit.IntegrationTest.item.comment;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    @Test
    void toCommentDto_shouldConvertCommentToDtoCorrectly() {
        User author = new User();
        author.setId(1L);
        author.setName("Alice");
        author.setEmail("alice@example.com");

        Item item = new Item();
        item.setId(10L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(author);

        Comment comment = new Comment();
        comment.setId(100L);
        comment.setText("Отличная вещь!");
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.of(2025, 10, 28, 10, 30));

        CommentDto dto = CommentMapper.toCommentDto(comment);

        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getText(), dto.getText());
        assertEquals(author.getName(), dto.getAuthorName());
        assertEquals(comment.getCreated(), dto.getCreated());
    }

    @Test
    void toCommentDtoList_shouldConvertListOfCommentsToDtoList() {
        User author = new User();
        author.setName("Bob");

        Item item = new Item();
        item.setOwner(author);

        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setText("Хорошо");
        comment1.setAuthor(author);
        comment1.setItem(item);
        comment1.setCreated(LocalDateTime.now());

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setText("Отлично");
        comment2.setAuthor(author);
        comment2.setItem(item);
        comment2.setCreated(LocalDateTime.now().plusDays(1));

        List<Comment> comments = List.of(comment1, comment2);

        List<CommentDto> dtos = CommentMapper.toCommentDtoList(comments);

        assertEquals(2, dtos.size());
        assertEquals("Хорошо", dtos.get(0).getText());
        assertEquals("Отлично", dtos.get(1).getText());
        assertEquals("Bob", dtos.get(0).getAuthorName());
        assertEquals("Bob", dtos.get(1).getAuthorName());
    }

    @Test
    void toCommentDtoList_shouldReturnEmptyList_whenInputIsEmpty() {

        List<Comment> emptyComments = List.of();

        List<CommentDto> dtos = CommentMapper.toCommentDtoList(emptyComments);

        assertTrue(dtos.isEmpty());
    }
}
