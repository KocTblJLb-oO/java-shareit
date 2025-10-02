package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemServiceImpl itemService;

    public ItemController(ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    // Добавление вещи
    @PostMapping
    public ItemDto createItem(@Valid @RequestBody Item item, @RequestHeader("X-Sharer-User-Id") Long owner) {
        log.info("Метод: {}. {}, {}", getMethod(), item, owner);
        return itemService.create(item, owner);
    }

    // Обновление вещи
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable("itemId") Long id, @RequestBody ItemDto newItem,
                              @RequestHeader("X-Sharer-User-Id") Long owner) {
        log.info("Метод: {}. {}, {}", getMethod(), newItem, owner);
        return itemService.update(id, newItem, owner);
    }

    // Получение вещи
    @GetMapping("/{itemId}")
    public ItemDto getUserById(@PathVariable("itemId") long id) {
        log.info("Метод: {}. {}", getMethod(), id);
        return itemService.getItemById(id);
    }

    // Получение всех вещей пользователя
    @GetMapping
    public List<ItemDto> getAllItemsFromUser(@RequestHeader("X-Sharer-User-Id") Long owner) {
        log.info("Метод: {}. {}", getMethod(), owner);
        return itemService.getAllItemsFromUser(owner);
    }

    // Поиск вещи
    @GetMapping("/search")
    public List<ItemDto> itemSearch(@RequestParam(value = "text") String text) {
        log.info("Метод: {}. {}", getMethod(), text);
        return itemService.itemSearch(text);
    }

    /*
 ------------------------------------------------ СЛУЖЕБНЫЕ МЕТОДЫ
*/

    // Возвращает имя метода для логирования
    private String getMethod() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }
}
