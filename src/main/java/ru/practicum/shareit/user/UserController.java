package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /*
        ------------------------------------------------ Работа с пользователями
    */

    // Добавление пользователя
    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto user) {
        log.info("Метод: createUser. Новый пользователь: {}", user);
        return userService.create(user);
    }

    // Обновление пользователя
    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") long id, @RequestBody UserDto newUser) {
        log.info("Метод: updateUser. Пользователь для обновления: {}", newUser);
        return userService.update(id, newUser);
    }

    // Получение пользователя
    @GetMapping("/{userId}")
    public UserDto findUserById(@PathVariable("userId") long id) {
        log.info("Метод: getUserById. Получение пользователей.");
        return userService.findUserById(id);
    }

    // Удаление пользователя
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") long id) {
        log.info("Метод: deleteUser. Удаление пользователя с id: {}", id);
        userService.deleteUser(id);
    }
}
