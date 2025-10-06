package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;

@Slf4j
@Service
public class UserService {

    private final UserStorageInMemory userStorageInMemory;

    public UserService(UserStorageInMemory userStorageInMemory) {
        this.userStorageInMemory = userStorageInMemory;
    }

    public UserDto create(UserDto user) {
        log.info("Метод: create. Новый пользователь: {}", user);
        validate(user);

        return UserMapper.toUserDto(userStorageInMemory.create(UserMapper.toUser(user)));
    }

    public UserDto update(long id, UserDto newUser) {
        log.info("Метод: update. Пользователь для обновления: {}", newUser);
        return UserMapper.toUserDto(userStorageInMemory.update(id, UserMapper.toUser(newUser)));
    }

    // Получение пользователя
    public UserDto findUserById(long id) {
        return UserMapper.toUserDto(userStorageInMemory.findUserById(id));
    }

    // Удаление пользователя
    public void deleteUser(long id) {
        log.info("Метод: deleteUser. Удаление пользователя с id: {}", id);
        userStorageInMemory.deleteUser(id);
    }

/*
   ------------------------------------------------ СЛУЖЕБНЫЕ МЕТОДЫ
*/

    // Проверка пользователя
    private void validate(UserDto user) {
        if (user.getEmail() == null) {
            String message = "Email: " + null + " - не может быть пустым";
            log.error(message);
            throw new ValidationException(message);
        }
    }
}
