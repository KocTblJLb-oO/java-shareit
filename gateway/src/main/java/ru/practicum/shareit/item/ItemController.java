
package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    // Добавление вещи
    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long owner) {
        log.info("Метод: createItem. {}, {}", itemDto, owner);
        return itemClient.create(itemDto, owner);
    }

    // Обновление вещи
    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable("itemId") Long id, @RequestBody ItemDto newItem,
                                             @RequestHeader("X-Sharer-User-Id") Long owner) {
        log.info("Метод: updateItem. {}, {}", newItem, owner);
        return itemClient.update(id, newItem, owner);
    }

    // Получение вещи
    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@PathVariable("itemId") long id, @RequestHeader("X-Sharer-User-Id") Long owner) {
        log.info("Метод: findUserById. {}", id);
        return itemClient.getItemById(id, owner);
    }

    // Получение всех вещей пользователя
    @GetMapping
    public List<ItemDto> getAllItemsFromUser(@RequestHeader("X-Sharer-User-Id") Long owner) {
        log.info("Метод: getAllItemsFromUser. {}", owner);
        return (List<ItemDto>) itemClient.getAllItems(owner);
    }

    // Поиск вещи
    @GetMapping("/search")
    public ResponseEntity<Object> itemSearch(@RequestParam(value = "text") String text) {
        log.info("Метод: itemSearch. {}", text);
        return itemClient.searchItems(text);
    }

    // Добавление комментария
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody Map<String, String> comment) {
        log.info("Метод: addComment. itemId {}, userId {}, comment {}", itemId, userId, comment);
        return itemClient.addComment(userId, itemId, comment.get("text"));
    }
}
