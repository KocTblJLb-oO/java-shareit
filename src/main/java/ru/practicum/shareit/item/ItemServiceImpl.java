package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;

    // Добавление вещи
    @Override
    public ItemDto create(ItemDto item, Long owner) {
        log.info("Метод: create. {}", item);
        validate(item, owner);
        item.setOwner(owner);

        return ItemMapper.toItemDto(itemStorage.create(ItemMapper.toItem(item)));
    }

    // Обновление вещи
    @Override
    public ItemDto update(Long id, ItemDto newItem, Long owner) {
        log.info("Метод: update. {}", newItem);
        validate(newItem, owner);
        newItem.setOwner(owner);
        return ItemMapper.toItemDto(itemStorage.update(id, ItemMapper.toItem(newItem)));
    }

    // Получение вещи
    @Override
    public ItemDto findUserById(long id) {
        log.info("Метод: findUserById. {}", id);
        return ItemMapper.toItemDto(itemStorage.getItemById(id));
    }

    // Получение всех вещей пользователя
    @Override
    public List<ItemDto> getAllItemsFromUser(Long owner) {
        log.info("Метод: getAllItemsFromUser. {}", owner);
        return itemStorage.getAllItemsFromUser(owner);
    }

    // Поиск вещи
    @Override
    public List<ItemDto> itemSearch(String text) {
        log.info("Метод: itemSearch. {}", text);

        if (text.isBlank()) {
            return Collections.emptyList();
        }

        return itemStorage.itemSearch(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }
        /*
    ------------------------------------------------ СЛУЖЕБНЫЕ МЕТОДЫ
*/

    // Проверка вещи
    private void validate(ItemDto item, Long owner) {
        if (owner == null) {
            String message = "Владелец не может быть пустым";
            log.error(message);
            throw new ValidationException(message);
        }
    }
}