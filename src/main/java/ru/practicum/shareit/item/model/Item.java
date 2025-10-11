package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner; //— владелец вещи;
    private Long request; //— если вещь была создана по запросу другого пользователя, то в это поле будет храниться ссылка на соответствующий запрос.
}
