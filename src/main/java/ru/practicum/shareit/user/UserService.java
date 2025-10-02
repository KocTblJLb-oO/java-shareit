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

    public UserDto create(User user) {
        log.info("Метод: {}. Новый пользователь: {}", getMethod(), user);
        validate(user);

        return userStorageInMemory.create(user);
    }


    public UserDto update(long id, User newUser) {
        log.info("Метод: {}. Пользователь для обновления: {}", getMethod(), newUser);
        return userStorageInMemory.update(id, newUser);
    }

    // Получение пользователя
    public UserDto getUserById(long id) {
        return userStorageInMemory.getUserById(id);
    }

    // Удаление пользователя
    public void deleteUser(long id) {
        log.info("Метод: {}. Удаление пользователя с id: {}", getMethod(), id);
        userStorageInMemory.deleteUser(id);
    }

    /*
            // Добавление в друзья
            public void addFriend(long id, long friendId) {
                log.info("Метод: {}. ID пользователя: {} ИД друга: {}", getMethod(), id, friendId);
                checkUser(id);
                checkUser(friendId);

                userStorage.addFriends(id, friendId);
            }

            // Удаление из друзей
            public void deleteFriend(long id, long friendId) {
                checkUser(id);
                checkUser(friendId);

                userStorage.deleteFriend(id, friendId);
            }

            // Получение списка друзей
            public List<User> getAllFriends(long id) {
                checkUser(id);
                return userStorage.getFriends(id);
            }

            // Список друзей, общих с другим пользователем
            public Collection<User> getCommonFriend(long id, long otherId) {
                checkUser(id);
                checkUser(otherId);

                return userStorage.getCommonFriend(id, otherId);
            }


            */
/*
   ------------------------------------------------ СЛУЖЕБНЫЕ МЕТОДЫ
*//*

    // Проверка существования пользователей
    private void checkUser(long id) {
        if (!userStorage.existsById(id)) {
            String message = "Пользователь с ID: " + id + " — не найден.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }
*/
    // Проверка пользователя
    private void validate(User user) {
        if (user.getEmail() == null) {
            String message = "Email: " + null + " - не может быть пустым";
            log.error(message);
            throw new ValidationException(message);
        }
    }


    // Возвращает имя метода для логирования
    private String getMethod() {
        return new Throwable().getStackTrace()[1].getMethodName();

    }
}
