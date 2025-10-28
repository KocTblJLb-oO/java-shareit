package ru.practicum.shareit.request;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDto dto, User requester) {
        return new ItemRequest(dto.getId(), dto.getDescription(), requester, LocalDateTime.now(), new ArrayList<>());
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest request) {
        return new ItemRequestDto(request.getId(), request.getDescription(), request.getRequester().getId(), request.getCreated(),
                request.getItems().stream()
                        .map(ItemMapper::toItemDto)
                        .toList()
        );
    }
}