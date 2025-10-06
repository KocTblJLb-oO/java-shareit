package ru.practicum.shareit.user;

public interface UserStorage {
    // Создание пользователя
    User create(User user);

    //Обновление пользователя
    User update(long id, User newUser);

    User findUserById(long id);

    // Удаление пользователя
    void deleteUser(long id);
}
