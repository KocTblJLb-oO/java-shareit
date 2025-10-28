package ru.practicum.shareit.IntegrationTest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

@RestController
public class TestController {

    @GetMapping("/test/validation")
    public void throwValidation() {
        throw new ValidationException("Ошибка валидации");
    }

    @GetMapping("/test/notfound")
    public void throwNotFound() {
        throw new NotFoundException("Не найдено");
    }

    @GetMapping("/test/conflict")
    public void throwConflict() {
        throw new DuplicatedDataException("Конфликт данных");
    }

    @GetMapping("/test/server")
    public void throwServer() {
        throw new RuntimeException("Сервер сломался");
    }
}