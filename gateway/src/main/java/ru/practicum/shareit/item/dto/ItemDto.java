package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.ArrayList;
import java.util.List;

@Data
public class ItemDto {

    public ItemDto(Long id, String name, String description, Boolean available, Long owner, Long request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.requestId = request;
        this.comments = new ArrayList<>();
    }

    public ItemDto() {
    }

    private Long id;
    @NotBlank(message = "Имя вещи не может быть пустым")
    private String name;
    @NotBlank(message = "Поле 'description' не может быть пустым")
    private String description;
    @NotNull(message = "Поле 'available' не может быть пустым")
    private Boolean available;
    private Long owner; //— владелец вещи;
    private Long requestId; //— если вещь была создана по запросу другого пользователя, то в это поле будет храниться ссылка на соответствующий запрос.
    private BookingDtoResponse lastBooking;
    private BookingDtoResponse nextBooking;
    private List<CommentDto> comments = new ArrayList<>();
}

