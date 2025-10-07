package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item create(Item item);

    // Обновление вещи
    Item update(Long id, Item newItem);

    Item getItemById(long id);

    List<ItemDto> getAllItemsFromUser(Long owner);

    // Поиск вещи
    List<Item> itemSearch(String text);
}
