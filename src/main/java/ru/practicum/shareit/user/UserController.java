package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /*
        ------------------------------------------------ Работа с пользователями
    */

    // Добавление пользователя
    @PostMapping
    public UserDto createUser(@Valid @RequestBody User user) {
        log.info("Метод: {}. Новый пользователь: {}", getMethod(), user);
        return userService.create(user);
    }

    // Обновление пользователя
    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") long id, @RequestBody User newUser) {
        log.info("Метод: {}. Пользователь для обновления: {}", getMethod(), newUser);
        return userService.update(id, newUser);
    }

    // Получение пользователя
    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable("userId") long id) {
        log.info("Метод: {}. Получение пользователей.", getMethod());
        return userService.getUserById(id);
    }

    // Удаление пользователя
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") long id) {
        log.info("Метод: {}. Удаление пользователя с id: {}", getMethod(), id);
        userService.deleteUser(id);
    }

    /*
    ------------------------------------------------ СЛУЖЕБНЫЕ МЕТОДЫ
*/

    // Возвращает имя метода для логирования
    private String getMethod() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }
}
