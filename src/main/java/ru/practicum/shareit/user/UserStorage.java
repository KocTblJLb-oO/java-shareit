package ru.practicum.shareit.user;

public interface UserStorage {
    // Создание пользователя
    UserDto create(User user);

    //Обновление пользователя
    UserDto update(long id, User newUser);

    UserDto getUserById(long id);

    // Удаление пользователя
    void deleteUser(long id);
}
