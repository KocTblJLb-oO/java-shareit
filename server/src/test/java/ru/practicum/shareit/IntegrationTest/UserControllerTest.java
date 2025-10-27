package ru.practicum.shareit.IntegrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void createUser_shouldReturnUserDto() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Имя");
        userDto.setEmail("alice@ya.ru");

        UserDto savedUser = new UserDto();
        savedUser.setId(1L);
        savedUser.setName("Имя");
        savedUser.setEmail("alice@ya.ru");

        when(userService.create(any(UserDto.class))).thenReturn(savedUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        UserDto newUser = new UserDto();
        newUser.setName("Имя новое");
        newUser.setEmail("alice2@ya.ru");

        UserDto updated = new UserDto();
        updated.setId(1L);
        updated.setName("Имя новое");
        updated.setEmail("alice2@ya.ru");

        when(userService.update(anyLong(), any(UserDto.class))).thenReturn(updated);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk());
    }

    @Test
    void findUserById_shouldReturnUser() throws Exception {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("Имя");
        user.setEmail("alice@ya.ru");

        when(userService.findUserById(anyLong())).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        // userService.deleteUser() возвращает void → контроллер тоже ничего не возвращает
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk()); // или status().isNoContent(), если возвращает 204
    }
}