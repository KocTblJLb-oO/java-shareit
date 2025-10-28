package ru.practicum.shareit.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
    private final String error;
    private final String response;

    public ErrorResponse(String error, String response) {
        this.error = error;
        this.response = response;
    }
}
