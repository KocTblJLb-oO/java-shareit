package ru.practicum.shareit.IntegrationTest.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ErrorResponse;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void errorResponse_shouldStoreFields() {
        ErrorResponse response = new ErrorResponse("Ошибка", "Сообщение");

        assertEquals("Ошибка", response.getError());
        assertEquals("Сообщение", response.getResponse());
    }
}