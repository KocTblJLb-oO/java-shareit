package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(
            @Valid @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.create(itemRequestDto, userId);
    }

    //  получить список своих запросов вместе с данными об ответах на них
    @GetMapping
    public List<ItemRequestDto> getOwnItemRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение списка собственных запросов для пользователя с id={}", userId);
        return itemRequestService.getOwnItemRequests(userId);
    }

    // получить список запросов, созданных другими пользователями
    @GetMapping("/all")
    public List<ItemRequestDto> getOtherItemRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /requests/all — получение чужих запросов для пользователя id={}", userId);
        return itemRequestService.getOtherItemRequests(userId);
    }

    //получить данные об одном конкретном запросе вместе с данными об ответах на него в том же формате, что и в эндпоинте GET /requests
    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId) {
        log.info("Получение данных о запросе с id={}, пользователь id={}", requestId, userId);
        return itemRequestService.getItemRequestById(requestId);
    }
}
