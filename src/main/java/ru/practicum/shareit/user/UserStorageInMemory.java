package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.HashMap;
import java.util.Optional;

@Repository
@Slf4j
public class UserStorageInMemory implements UserStorage {
    private Long currentMaxId = 0L;
    private final HashMap<Long, User> userHashMap = new HashMap<>();

    // Создание пользователя
    @Override
    public User create(User user) {
        log.info("Метод: create. Новый пользователь: {}", user);
        log.debug("Было пользователей: {}", userHashMap.size());
        validateUniqueEmail(user);
        long newId = getNextId();

        user.setId(newId);
        userHashMap.put(newId, user);

        log.debug("Стало пользователей: {}", userHashMap.size());
        return user;
    }

    //Обновление пользователя
    @Override
    public User update(long id, User newUser) {
        log.info("Метод: update. Пользователь для обновления: {}", newUser);

        newUser.setId(id);

        if (newUser.getEmail() != null) { // Если почта не пустая, то проверяем её на дублика и обновляем пользователя
            validateUniqueEmail(newUser);
            userHashMap.put(newUser.getId(), newUser);
        } else { // Если почта пустая, то обновляем только имя
            User oldUser = userHashMap.get(newUser.getId());
            oldUser.setName(newUser.getName());
            userHashMap.put(newUser.getId(), oldUser);
        }


        return userHashMap.get(newUser.getId());
    }

    @Override
    public User findUserById(long id) {
        if (!userHashMap.containsKey(id)) {
            String message = "Владелец: " + id + " - не существует";
            log.error(message);
            throw new NotFoundException(message);
        }
        return userHashMap.get(id);
    }

    // Удаление пользователя
    @Override
    public void deleteUser(long id) {
        log.info("Метод: deleteUser. Удаление пользователя с id: {}", id);
        userHashMap.remove(id);
    }

    /*
   ------------------------------------------------ СЛУЖЕБНЫЕ МЕТОДЫ
*/
    // Создание нового ID
    private long getNextId() {
        return ++currentMaxId;
    }

    public void validateUniqueEmail(User newUser) {
        log.info("Метод: validateUniqueEmail. {}", newUser);
        Optional<User> findUser = userHashMap.values().stream()
                .filter(user -> user.getEmail().equals(newUser.getEmail()))
                .findFirst();
        if (findUser.isPresent()) {
            String message = "Email: " + newUser.getEmail() + " - уже существует";
            log.error(message);
            throw new DuplicatedDataException(message);
        }
    }
}
