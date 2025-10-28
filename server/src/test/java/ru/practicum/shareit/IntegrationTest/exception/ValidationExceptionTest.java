package ru.practicum.shareit.IntegrationTest.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

class ValidationExceptionTest {

    @Test
    void validationException_shouldStoreMessage() {
        String message = "Некорректные данные";
        ValidationException exception = new ValidationException(message);

        assertEquals(message, exception.getMessage());
    }
}