package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private long id;
    @Email
    @NotBlank(message = "Поле 'email' не может быть пустым")
    private String email;
    @NotBlank(message = "Поле 'name' не может быть пустым")
    private String name;
}
