package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto create(Item item, Long owner);

    // Обновление вещи
    ItemDto update(Long id, ItemDto newItem, Long owner);

    ItemDto getItemById(long id);

    List<ItemDto> getAllItemsFromUser(Long owner);

    // Поиск вещи
    List<ItemDto> itemSearch(String text);
}
