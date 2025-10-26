package ru.practicum.shareit.IntegrationTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserService userService; // предполагается, что UserService доступен

    @Test
    void create_shouldSaveItemRequestAndReturnWithId() {
        // 1. Создаём пользователя
        UserDto userDto = new UserDto();
        userDto.setName("Alice");
        userDto.setEmail("alice@example.com");
        UserDto savedUser = userService.create(userDto);
        Long userId = savedUser.getId();

        // 2. Создаём запрос
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Нужна дрель");

        // 3. Вызываем тестируемый метод
        ItemRequestDto savedRequest = itemRequestService.create(requestDto, userId);

        // 4. Проверяем результат
        assertThat(savedRequest).isNotNull();
        assertThat(savedRequest.getId()).isNotNull();
        assertThat(savedRequest.getDescription()).isEqualTo("Нужна дрель");
    }
}