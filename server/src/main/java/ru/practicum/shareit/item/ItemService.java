package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto itemDto, Long owner);

    // Обновление вещи
    ItemDto update(Long id, ItemDto newItem, Long owner);

    ItemDto findItemById(long id, long owner);

    ItemDto findItemById(long id);

    List<ItemDto> getAllItemsFromUser(Long owner);

    // Поиск вещи
    List<ItemDto> itemSearch(String text);

    CommentDto addComment(Long userId, Long itemId, String text);
}
