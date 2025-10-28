package ru.practicum.shareit.IntegrationTest.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class NotFoundExceptionTest {

    @Test
    void notFoundException_shouldStoreMessage() {
        String message = "Пользователь не найден";
        NotFoundException exception = new NotFoundException(message);

        assertEquals(message, exception.getMessage());
    }
}