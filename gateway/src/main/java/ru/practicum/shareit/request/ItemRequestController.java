package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @Valid @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.create(itemRequestDto, userId);
    }

    //  получить список своих запросов вместе с данными об ответах на них
    @GetMapping
    public ResponseEntity<Object> getOwnItemRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение списка собственных запросов для пользователя с id={}", userId);
        return itemRequestClient.getItemRequests(userId);
    }

    // получить список запросов, созданных другими пользователями
    @GetMapping("/all")
    public ResponseEntity<Object> getOtherItemRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /requests/all — получение чужих запросов для пользователя id={}", userId);
        return itemRequestClient.getAllItemRequests(userId);
    }

    //получить данные об одном конкретном запросе вместе с данными об ответах на него в том же формате, что и в эндпоинте GET /requests
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId) {
        log.info("Получение данных о запросе с id={}, пользователь id={}", requestId, userId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}
