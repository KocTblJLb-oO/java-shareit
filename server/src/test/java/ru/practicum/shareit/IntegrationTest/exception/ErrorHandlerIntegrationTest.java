package ru.practicum.shareit.IntegrationTest.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ru.practicum.shareit.IntegrationTest.TestController.class)
class ErrorHandlerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldHandleValidationException() throws Exception {
        mockMvc.perform(get("/test/validation"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Ошибка"))
                .andExpect(jsonPath("$.response").value("Ошибка валидации"));
    }

    @Test
    void shouldHandleNotFoundException() throws Exception {
        mockMvc.perform(get("/test/notfound"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Ошибка"))
                .andExpect(jsonPath("$.response").value("Не найдено"));
    }

    @Test
    void shouldHandleDuplicatedDataException() throws Exception {
        mockMvc.perform(get("/test/conflict"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Ошибка"))
                .andExpect(jsonPath("$.response").value("Конфликт данных"));
    }

    @Test
    void shouldHandleGenericException() throws Exception {
        mockMvc.perform(get("/test/server"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Ошибка"))
                .andExpect(jsonPath("$.response", startsWith("Произошла непредвиденная ошибка")));
    }
}