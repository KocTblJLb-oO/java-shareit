package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;


/*
        ------------------------------------------------ Работа с пользователями
    */


    // Добавление пользователя
    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto user) {
     //   log.info("Метод: createUser. Новый пользователь: {}", user);
        return userClient.create(user);
    }

    // Обновление пользователя
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable("userId") long id, @RequestBody UserDto newUser) {
       // log.info("Метод: updateUser. Пользователь для обновления: {}", newUser);
        return userClient.update(id, newUser);
    }

    // Получение пользователя
    @GetMapping("/{userId}")
    public ResponseEntity<Object> findUserById(@PathVariable("userId") long id) {
      //  log.info("Метод: getUserById. Получение пользователей.");
        return userClient.getUserById(id);
    }

    // Удаление пользователя
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") long id) {
      //  log.info("Метод: deleteUser. Удаление пользователя с id: {}", id);
        userClient.delete(id);
    }
}

