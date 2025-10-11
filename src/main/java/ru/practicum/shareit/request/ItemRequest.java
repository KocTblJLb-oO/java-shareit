package ru.practicum.shareit.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ItemRequest {
    private long id;
    String description;
    long requestor; // — пользователь, создавший запрос
    LocalDate created;
}
