package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ItemStorageInMemory implements ItemStorage {
    private Long currentMaxId = 0L;
    private final HashMap<Long, Item> itemHashMap = new HashMap<>();

    // Добавление вещи
    @Override
    public Item create(@Valid Item item) {
        log.info("Метод: create. {}", item);
        log.debug("Было вещей: {}", itemHashMap.size());
        validateItem(item);
        long newId = getNextId();

        item.setId(newId);
        itemHashMap.put(newId, item);

        log.debug("Стало вещей: {}", itemHashMap.size());
        return itemHashMap.get(item.getId());
    }

    // Обновление вещи
    @Override
    public Item update(Long id, Item newItem) {
        log.info("Метод: update. {}", newItem);
        validateItem(newItem);
        Item oldItem = itemHashMap.get(id);

        if (newItem.getName() != null && !newItem.getName().isBlank()) {
            oldItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null && !newItem.getDescription().isBlank()) {
            oldItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }
        if (newItem.getRequest() != null) {
            oldItem.setRequest(newItem.getRequest());
        }

        itemHashMap.put(id, oldItem);
        return itemHashMap.get(id);
    }

    // Получение вещи
    @Override
    public Item getItemById(long id) {
        if (!itemHashMap.containsKey(id)) {
            String message = "Вещь: " + id + " - не найдена ";
            log.error(message);
            throw new NotFoundException(message);
        }

        return itemHashMap.get(id);
    }

    // Получение всех вещей пользователя
    @Override
    public List<ItemDto> getAllItemsFromUser(Long owner) {
        log.info("Метод: getAllItemsFromUser. {}", owner);

        return itemHashMap.values().stream()
                .filter(item -> item.getOwner().equals(owner))
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<Item> itemSearch(String text) {
        log.info("Метод: itemSearch. {}", text);

        return itemHashMap.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .toList();
    }

    /*
------------------------------------------------ СЛУЖЕБНЫЕ МЕТОДЫ
*/
    // Создание нового ID
    private long getNextId() {
        return ++currentMaxId;
    }

    // Проверка вещи
    private void validateItem(Item item) {
        log.debug("Метод: validateItem. {}", item);
        // Проверка владельца при обновлении
        if (itemHashMap.containsKey(item.getId())) {
            log.debug("Вещь существует");
            if (!itemHashMap.get(item.getId()).getOwner().equals(item.getOwner())) {
                String message = "Владелец: " + item.getOwner() + " - не cоответствует реальному владельцу: "
                        + itemHashMap.get(item.getId()).getOwner();
                log.error(message);
                throw new NotFoundException(message);
            }
        }
    }

}
