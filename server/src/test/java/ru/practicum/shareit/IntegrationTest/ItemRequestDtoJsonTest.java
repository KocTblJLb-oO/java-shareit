package ru.practicum.shareit.IntegrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Нужна дрель");
        dto.setRequester(2L);
        dto.setCreated(LocalDateTime.of(2025, Month.OCTOBER, 25, 22, 0, 0));

        String jsonStr = objectMapper.writeValueAsString(dto);

        assertThat(jsonStr).contains("\"created\":\"2025-10-25T22:00:00\"");
        assertThat(jsonStr).contains("\"description\":\"Нужна дрель\"");
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonStr = "{\n" +
                "  \"id\": 1,\n" +
                "  \"description\": \"Нужна дрель\",\n" +
                "  \"requester\": 2,\n" +
                "  \"created\": \"2025-10-25T22:00:00\"\n" +
                "}";

        ItemRequestDto dto = objectMapper.readValue(jsonStr, ItemRequestDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Нужна дрель");
        assertThat(dto.getRequester()).isEqualTo(2L);
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2025, 10, 25, 22, 0, 0));
    }
}
