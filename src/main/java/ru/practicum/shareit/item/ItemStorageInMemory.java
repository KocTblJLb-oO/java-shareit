package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorageInMemory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Repository
@Slf4j
public class ItemStorageInMemory implements ItemStorage {
    private Long currentMaxId = 0L;
    private final HashMap<Long, Item> itemHashMap = new HashMap<>();
    UserStorageInMemory userStorageInMemory;

    public ItemStorageInMemory(UserStorageInMemory userStorageInMemory) {
        this.userStorageInMemory = userStorageInMemory;
    }

    // Добавление вещи
    @Override
    public ItemDto create(@Valid Item item) {
        log.info("Метод: {}. {}", getMethod(), item);
        log.debug("Было вещей: {}", itemHashMap.size());
        validateItem(item);
        long newId = getNextId();

        item.setId(newId);
        itemHashMap.put(newId, item);

        log.debug("Стало вещей: {}", itemHashMap.size());
        return ItemMapper.toItemDto(itemHashMap.get(item.getId()));
    }

    // Обновление вещи
    @Override
    public ItemDto update(Long id, ItemDto newItem) {
        log.info("Метод: {}. {}", getMethod(), newItem);
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
        return ItemMapper.toItemDto(itemHashMap.get(id));
    }

    // Получение вещи
    @Override
    public ItemDto getItemById(long id) {
        return ItemMapper.toItemDto(itemHashMap.get(id));
    }

    // Получение всех вещей пользователя
    @Override
    public List<ItemDto> getAllItemsFromUser(Long owner) {
        log.info("Метод: {}. {}", getMethod(), owner);

        return itemHashMap.values().stream()
                .filter(item -> item.getOwner().equals(owner))
                .map(ItemMapper::toItemDto)
                .toList();
    }

    // Поиск вещи
    public List<ItemDto> itemSearch(String text) {
        log.info("Метод: {}. {}", getMethod(), text);
        if(text.isBlank()){
            return Collections.emptyList();
        }
        return itemHashMap.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .map(ItemMapper::toItemDto)
                .toList();
    }

    /*
------------------------------------------------ СЛУЖЕБНЫЕ МЕТОДЫ
*/
    // Создание нового ID
    private long getNextId() {
        return ++currentMaxId;
    }

    // Возвращает имя метода для логирования
    private String getMethod() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }

    // Проверка вещи
    private void validateItem(Item item) {
        log.debug("Метод: {}. {}", getMethod(), item);
        // Если пользователя нет, исключение будет в методе getUserById
        userStorageInMemory.getUserById(item.getOwner());
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

    private void validateItem(ItemDto item) {
        validateItem(ItemMapper.dtoToItem(item));
    }

}
