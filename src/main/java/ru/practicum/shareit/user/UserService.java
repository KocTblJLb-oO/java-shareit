package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserStorage userStorage;

    @Transactional
    public UserDto create(UserDto user) {
        log.info("Метод: create. Новый пользователь: {}", user);
        UserDto newUser = UserMapper.toUserDto(userStorage.save(UserMapper.toUser(user)));
        log.debug("Метод: create. Созданный пользователь: {}", newUser);
        return newUser;
    }

    @Transactional
    public UserDto update(long id, UserDto newUser) {
        log.info("Метод: update. Пользователь для обновления: {}, ИД: {}", newUser, id);
        User oldUser = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (newUser.getName() != null && !newUser.getName().isBlank()) {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) {
            oldUser.setEmail(newUser.getEmail());
        }

        return UserMapper.toUserDto(userStorage.save(oldUser));
    }

    // Получение пользователя
    @Transactional
    public UserDto findUserById(long id) {
        return UserMapper.toUserDto(userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден")));
    }

    // Удаление пользователя
    @Transactional
    public void deleteUser(long id) {
        log.info("Метод: deleteUser. Удаление пользователя с id: {}", id);
        userStorage.deleteById(id);
    }
}
