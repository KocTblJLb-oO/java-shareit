package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    ItemDto create(Item item);

    // Обновление вещи
    ItemDto update(Long id, ItemDto newItem);

    ItemDto getItemById(long id);

    List<ItemDto> getAllItemsFromUser(Long owner);
}
