package ru.practicum.shareit.IntegrationTest.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void create_shouldSaveUserAndReturnWithId() {
        UserDto userDto = new UserDto();
        userDto.setName("Имя");
        userDto.setEmail("alice@ya.ru");

        UserDto savedUser = userService.create(userDto);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Имя");
        assertThat(savedUser.getEmail()).isEqualTo("alice@ya.ru");
    }
}
