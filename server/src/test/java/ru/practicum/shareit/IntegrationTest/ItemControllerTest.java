package ru.practicum.shareit.IntegrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void createItem_shouldReturnItemDto() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Название");
        itemDto.setDescription("Описание");
        itemDto.setAvailable(true);

        ItemDto saved = new ItemDto();
        saved.setId(1L);
        saved.setName("Название");
        saved.setDescription("Описание");
        saved.setAvailable(true);

        when(itemService.create(any(ItemDto.class), anyLong())).thenReturn(saved);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateItem_shouldReturnUpdatedItem() throws Exception {
        ItemDto newItem = new ItemDto();
        newItem.setName("Новое название");
        newItem.setAvailable(false);

        ItemDto updated = new ItemDto();
        updated.setId(1L);
        updated.setName("Новое название");
        updated.setAvailable(false);

        when(itemService.update(anyLong(), any(ItemDto.class), anyLong())).thenReturn(updated);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isOk());
    }

    @Test
    void findItemById_shouldReturnItem() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("Название");

        when(itemService.findItemById(anyLong(), anyLong())).thenReturn(item);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void getAllItemsFromUser_shouldReturnListOfItems() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("Название");

        when(itemService.getAllItemsFromUser(anyLong()))
                .thenReturn(Collections.singletonList(item));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void itemSearch_shouldReturnListOfItems() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("Название");

        when(itemService.itemSearch(anyString()))
                .thenReturn(Collections.singletonList(item));

        mockMvc.perform(get("/items/search")
                        .param("text", "Название"))
                .andExpect(status().isOk());
    }

    @Test
    void addComment_shouldReturnCommentDto() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Комментарий");

        when(itemService.addComment(anyLong(), anyLong(), anyString()))
                .thenReturn(commentDto);

        Map<String, String> comment = Map.of("text", "Комментарий");

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk());
    }
}