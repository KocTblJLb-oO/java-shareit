package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStorageInMemory itemStorageInMemory;

    public ItemServiceImpl(ItemStorageInMemory itemStorageInMemory) {
        this.itemStorageInMemory = itemStorageInMemory;
    }

    // Добавление вещи
    @Override
    public ItemDto create(@Valid Item item, Long owner) {
        log.info("Метод: {}. {}", getMethod(), item);
        validate(item, owner);
        item.setOwner(owner);

        return itemStorageInMemory.create(item);
    }

    // Обновление вещи
    @Override
    public ItemDto update(Long id, ItemDto newItem, Long owner) {
        log.info("Метод: {}. {}", getMethod(), newItem);
        validate(newItem, owner);
        newItem.setOwner(owner);
        return itemStorageInMemory.update(id, newItem);
    }

    // Получение вещи
    @Override
    public ItemDto getItemById(long id) {
        log.info("Метод: {}. {}", id);
        return itemStorageInMemory.getItemById(id);
    }

    // Получение всех вещей пользователя
    @Override
    public List<ItemDto> getAllItemsFromUser(Long owner) {
        log.info("Метод: {}. {}", getMethod(), owner);
        return itemStorageInMemory.getAllItemsFromUser(owner);
    }

    // Поиск вещи
    @Override
    public List<ItemDto> itemSearch(String text) {
        log.info("Метод: {}. {}", getMethod(), text);
        return itemStorageInMemory.itemSearch(text);
    }
        /*
    ------------------------------------------------ СЛУЖЕБНЫЕ МЕТОДЫ
*/

    // Возвращает имя метода для логирования
    private String getMethod() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }

    // Проверка вещи
    private void validate(ItemDto item, Long owner) {
        if (owner == null) {
            String message = "Владелец: " + owner + " - не может быть пустым";
            log.error(message);
            throw new ValidationException(message);
        }
    }

    private void validate(Item item, Long owner) {
        if (owner == null) {
            String message = "Владелец: " + owner + " - не может быть пустым";
            log.error(message);
            throw new ValidationException(message);
        }
    }
}