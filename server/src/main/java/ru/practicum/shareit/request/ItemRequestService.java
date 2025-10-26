package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestService {

    private final ItemRequestStorage itemRequestStorage;
    private final UserService userService;
    private final ItemStorage itemStorage;


    @Transactional
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        User requester = UserMapper.toUser(userService.findUserById(userId));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requester);
        ItemRequest savedRequest = itemRequestStorage.save(itemRequest);
        return ItemRequestMapper.toItemRequestDto(savedRequest);
    }

    //  получить список своих запросов вместе с данными об ответах на них
    public List<ItemRequestDto> getOwnItemRequests(Long userId) {
        log.info("Получение списка собственных запросов для пользователя с id={}", userId);

        List<ItemRequest> requests = itemRequestStorage.findByRequesterIdOrderByIdDesc(userId);

        List<Item> items = itemStorage.findByRequestIdIn(
                requests.stream().map(ItemRequest::getId).toList()
        );

        Map<Long, List<Item>> itemsByRequestId = items.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return requests.stream()
                .map(request -> {
                    List<ItemDto> itemDto = itemsByRequestId.getOrDefault(request.getId(), List.of())
                            .stream()
                            .map(ItemMapper::toItemDto)
                            .collect(Collectors.toList());

                    return new ItemRequestDto(
                            request.getId(),
                            request.getDescription(),
                            request.getRequester().getId(),
                            request.getCreated(),
                            itemDto
                    );
                })
                .sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed())
                .collect(Collectors.toList());
    }

    // получить список запросов, созданных другими пользователями
    public List<ItemRequestDto> getOtherItemRequests(Long userId) {
        log.info("Получение запросов, созданных другими пользователями для userId={}", userId);

        List<ItemRequest> requests = itemRequestStorage.findByRequesterIdNotOrderByIdDesc(userId);

        List<Item> items = itemStorage.findByRequestIdIn(
                requests.stream().map(ItemRequest::getId).toList()
        );

        Map<Long, List<Item>> itemsByRequestId = items.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return requests.stream()
                .map(request -> {
                    List<ItemDto> itemDtos = itemsByRequestId.getOrDefault(request.getId(), List.of())
                            .stream()
                            .map(ItemMapper::toItemDto)
                            .collect(Collectors.toList());

                    return new ItemRequestDto(
                            request.getId(),
                            request.getDescription(),
                            request.getRequester().getId(),
                            request.getCreated(),
                            itemDtos
                    );
                })
                .collect(Collectors.toList());
    }

    //получить данные об одном конкретном запросе вместе с данными об ответах на него в том же формате, что и в эндпоинте GET /requests
    public ItemRequestDto getItemRequestById(Long requestId) {

        ItemRequest request = itemRequestStorage.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id=" + requestId + " не найден"));

        List<Item> items = itemStorage.findByRequestId(requestId);

        List<ItemDto> itemDtos = items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getRequester().getId(),
                request.getCreated(),
                itemDtos
        );
    }
}