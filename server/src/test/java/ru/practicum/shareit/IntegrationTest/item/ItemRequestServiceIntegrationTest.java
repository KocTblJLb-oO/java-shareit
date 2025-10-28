package ru.practicum.shareit.IntegrationTest.item;

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
    private UserService userService;

    @Test
    void create_shouldSaveItemRequestAndReturnWithId() {
        UserDto userDto = new UserDto();
        userDto.setName("Alice");
        userDto.setEmail("alice@example.com");
        UserDto savedUser = userService.create(userDto);
        Long userId = savedUser.getId();

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Нужна дрель");

        ItemRequestDto savedRequest = itemRequestService.create(requestDto, userId);

        assertThat(savedRequest).isNotNull();
        assertThat(savedRequest.getId()).isNotNull();
        assertThat(savedRequest.getDescription()).isEqualTo("Нужна дрель");
    }
}