package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    // Добавление вещи
    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long owner) {
        log.info("Метод: createItem. {}, {}", itemDto, owner);
        return itemService.create(itemDto, owner);
    }

    // Обновление вещи
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable("itemId") Long id, @RequestBody ItemDto newItem,
                              @RequestHeader("X-Sharer-User-Id") Long owner) {
        log.info("Метод: updateItem. {}, {}", newItem, owner);
        return itemService.update(id, newItem, owner);
    }

    // Получение вещи
    @GetMapping("/{itemId}")
    public ItemDto findItemById(@PathVariable("itemId") long id, @RequestHeader("X-Sharer-User-Id") Long owner) {
        log.info("Метод: findUserById. {}", id);
        return itemService.findItemById(id, owner);
    }

    // Получение всех вещей пользователя
    @GetMapping
    public List<ItemDto> getAllItemsFromUser(@RequestHeader("X-Sharer-User-Id") Long owner) {
        log.info("Метод: getAllItemsFromUser. {}", owner);
        return itemService.getAllItemsFromUser(owner);
    }

    // Поиск вещи
    @GetMapping("/search")
    public List<ItemDto> itemSearch(@RequestParam(value = "text") String text) {
        log.info("Метод: itemSearch. {}", text);
        return itemService.itemSearch(text);
    }

    // Добавление комментария
    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody Map<String, String> comment) {
        log.info("Метод: addComment. itemId {}, userId {}, comment {}",itemId, userId, comment);
        return itemService.addComment(userId, itemId, comment.get("text"));
    }
}