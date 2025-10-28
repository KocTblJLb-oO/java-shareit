package ru.practicum.shareit.IntegrationTest.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.ItemRequestStorage;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRequestStorage itemRequestStorage;

    @Mock
    private UserService userService;

    @Mock
    private ItemStorage itemStorage;

    @InjectMocks
    private ItemRequestService itemRequestService;

    private User requester;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        requester = new User();
        requester.setId(1L);
        requester.setName("Requester");
        requester.setEmail("requester@example.com");

        itemRequest = new ItemRequest();
        itemRequest.setId(10L);
        itemRequest.setDescription("Нужна дрель");
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Нужна дрель");
    }

    @Test
    void create_shouldCreateItemRequestSuccessfully() {
        UserDto requesterDto = new UserDto(1L, "Requester", "requester@example.com");
        when(userService.findUserById(1L)).thenReturn(requesterDto);

        ItemRequestDto inputDto = new ItemRequestDto();
        inputDto.setDescription("Нужна дрель");

        ItemRequest expectedRequest = ItemRequestMapper.toItemRequest(inputDto, new User(1L, "Requester", "requester@example.com"));
        expectedRequest.setId(10L);

        when(itemRequestStorage.save(any(ItemRequest.class))).thenAnswer(invocation -> {
            ItemRequest saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        ItemRequestDto result = itemRequestService.create(inputDto, 1L);

        assertNotNull(result);
        assertEquals("Нужна дрель", result.getDescription());
        assertEquals(10L, result.getId());
        verify(itemRequestStorage).save(any(ItemRequest.class));
    }

    @Test
    void getOwnItemRequests_shouldReturnRequestsWithItems() {
        Item item = new Item();
        item.setId(20L);
        item.setName("Дрель");
        item.setRequest(itemRequest);
        item.setOwner(requester);

        when(itemRequestStorage.findByRequesterIdOrderByIdDesc(1L))
                .thenReturn(List.of(itemRequest));
        when(itemStorage.findByRequestIdIn(List.of(10L)))
                .thenReturn(List.of(item));

        List<ItemRequestDto> result = itemRequestService.getOwnItemRequests(1L);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getItems().size());
        assertEquals("Дрель", result.get(0).getItems().get(0).getName());
    }

    @Test
    void getOwnItemRequests_shouldReturnEmptyList_whenNoRequests() {
        when(itemRequestStorage.findByRequesterIdOrderByIdDesc(1L))
                .thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = itemRequestService.getOwnItemRequests(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getOtherItemRequests_shouldReturnRequestsFromOtherUsers() {
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setName("Other");
        otherUser.setEmail("other@example.com");

        ItemRequest otherRequest = new ItemRequest();
        otherRequest.setId(11L);
        otherRequest.setDescription("Нужен молоток");
        otherRequest.setRequester(otherUser);
        otherRequest.setCreated(LocalDateTime.now());

        Item item = new Item();
        item.setId(21L);
        item.setName("Молоток");
        item.setRequest(otherRequest);
        item.setOwner(otherUser);

        when(itemRequestStorage.findByRequesterIdNotOrderByIdDesc(1L))
                .thenReturn(List.of(otherRequest));
        when(itemStorage.findByRequestIdIn(List.of(11L)))
                .thenReturn(List.of(item));

        List<ItemRequestDto> result = itemRequestService.getOtherItemRequests(1L);

        assertEquals(1, result.size());
        assertEquals("Нужен молоток", result.get(0).getDescription());
        assertEquals(1, result.get(0).getItems().size());
    }

    @Test
    void getOtherItemRequests_shouldReturnEmptyList_whenNoOtherRequests() {
        when(itemRequestStorage.findByRequesterIdNotOrderByIdDesc(1L))
                .thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = itemRequestService.getOtherItemRequests(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getItemRequestById_shouldReturnRequestWithItems() {
        Item item = new Item();
        item.setId(20L);
        item.setName("Дрель");
        item.setRequest(itemRequest);
        item.setOwner(requester);

        when(itemRequestStorage.findById(10L))
                .thenReturn(Optional.of(itemRequest));
        when(itemStorage.findByRequestId(10L))
                .thenReturn(List.of(item));

        ItemRequestDto result = itemRequestService.getItemRequestById(10L);

        assertNotNull(result);
        assertEquals("Нужна дрель", result.getDescription());
        assertEquals(1, result.getItems().size());
        assertEquals("Дрель", result.getItems().get(0).getName());
    }

    @Test
    void getItemRequestById_shouldThrowNotFound_whenRequestDoesNotExist() {
        when(itemRequestStorage.findById(999L))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestById(999L));
        assertEquals("Запрос с id=999 не найден", exception.getMessage());
    }

    private ru.practicum.shareit.user.UserDto toUserDto(User user) {
        return new ru.practicum.shareit.user.UserDto(user.getId(), user.getName(), user.getEmail());
    }
}