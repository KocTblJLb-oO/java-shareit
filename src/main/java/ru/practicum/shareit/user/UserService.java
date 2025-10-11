package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorageInMemory userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto create(UserDto user) {
        log.info("Метод: create. Новый пользователь: {}", user);
        validate(user);

        return UserMapper.toUserDto(userStorage.create(UserMapper.toUser(user)));
    }

    public UserDto update(long id, UserDto newUser) {
        log.info("Метод: update. Пользователь для обновления: {}", newUser);
        return UserMapper.toUserDto(userStorage.update(id, UserMapper.toUser(newUser)));
    }

    // Получение пользователя
    public UserDto findUserById(long id) {
        return UserMapper.toUserDto(userStorage.findUserById(id));
    }

    // Удаление пользователя
    public void deleteUser(long id) {
        log.info("Метод: deleteUser. Удаление пользователя с id: {}", id);
        userStorage.deleteUser(id);
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
