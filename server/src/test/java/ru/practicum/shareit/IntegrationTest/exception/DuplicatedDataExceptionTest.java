package ru.practicum.shareit.IntegrationTest.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.DuplicatedDataException;

import static org.junit.jupiter.api.Assertions.*;

class DuplicatedDataExceptionTest {

    @Test
    void duplicatedDataException_shouldStoreMessage() {
        String message = "Email уже используется";
        DuplicatedDataException exception = new DuplicatedDataException(message);

        assertEquals(message, exception.getMessage());
    }
}