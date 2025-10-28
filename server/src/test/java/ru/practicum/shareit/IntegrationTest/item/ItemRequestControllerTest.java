package ru.practicum.shareit.IntegrationTest.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void createItemRequest_shouldReturnItemRequestDto() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Нужна дрель");

        ItemRequestDto saved = new ItemRequestDto();
        saved.setId(1L);
        saved.setDescription("Нужна дрель");

        when(itemRequestService.create(any(ItemRequestDto.class), anyLong()))
                .thenReturn(saved);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void getOwnItemRequests_shouldReturnListOfItemRequestDto() throws Exception {
        ItemRequestDto request = new ItemRequestDto();
        request.setId(1L);
        request.setDescription("Нужна дрель");

        when(itemRequestService.getOwnItemRequests(anyLong()))
                .thenReturn(Collections.singletonList(request));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void getOtherItemRequests_shouldReturnListOfItemRequestDto() throws Exception {
        ItemRequestDto request = new ItemRequestDto();
        request.setId(2L);
        request.setDescription("Нужен шуруповёрт");

        when(itemRequestService.getOtherItemRequests(anyLong()))
                .thenReturn(Collections.singletonList(request));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void getItemRequestById_shouldReturnItemRequestDto() throws Exception {
        ItemRequestDto request = new ItemRequestDto();
        request.setId(3L);
        request.setDescription("Нужна пила");

        when(itemRequestService.getItemRequestById(anyLong()))
                .thenReturn(request);

        mockMvc.perform(get("/requests/3")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }
}