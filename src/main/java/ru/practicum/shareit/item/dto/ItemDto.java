package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ItemDto {

    private Long id;
    @NotNull(message = "Поле 'name' не может быть пустым")
    @NotBlank(message = "Имя вещи не может быть пустым")
    private String name;
    @NotNull(message = "Поле 'description' не может быть пустым")
    private String description;
    @NotNull(message = "Поле 'available' не может быть пустым")
    private Boolean available;
    private Long owner; //— владелец вещи;
    private Long request; //— если вещь была создана по запросу другого пользователя, то в это поле будет храниться ссылка на соответствующий запрос.
}

