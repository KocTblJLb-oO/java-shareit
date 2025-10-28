package ru.practicum.shareit.IntegrationTest.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.other.StatusOfBooking;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestStorage;
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
class ItemServiceImplTest {

    @Mock
    private ItemStorage itemStorage;

    @Mock
    private UserService userService;

    @Mock
    private BookingStorage bookingStorage;

    @Mock
    private CommentStorage commentStorage;

    @Mock
    private ItemRequestStorage itemRequestStorage;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private Item item;
    private ItemDto itemDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        itemRequest = new ItemRequest();
        itemRequest.setId(5L);

        item = new Item();
        item.setId(10L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(itemRequest);

        itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(5L);
    }


    @Test
    void create_shouldCreateItemSuccessfully() {
        // given
        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        UserDto ownerDto = new UserDto();
        ownerDto.setId(1L);
        ownerDto.setName("Owner");
        ownerDto.setEmail("owner@example.com");

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(5L);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(5L);

        Item savedItem = new Item();
        savedItem.setId(10L);
        savedItem.setName("Item");
        savedItem.setDescription("Description");
        savedItem.setAvailable(true);
        savedItem.setOwner(owner);
        savedItem.setRequest(itemRequest);

        when(userService.findUserById(1L)).thenReturn(ownerDto); // ✅ Возвращаем UserDto
        when(itemRequestStorage.findById(5L)).thenReturn(Optional.of(itemRequest));
        when(itemStorage.save(any(Item.class))).thenReturn(savedItem);

        ItemDto result = itemService.create(itemDto, 1L);

        assertNotNull(result);
        assertEquals("Item", result.getName());
        verify(itemStorage).save(any(Item.class));
    }

    @Test
    void create_shouldThrowNotFound_ifItemRequestNotFound() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        UserDto ownerDto = new UserDto();
        ownerDto.setId(1L);
        ownerDto.setName("Owner");
        ownerDto.setEmail("owner@example.com");

        ItemDto itemDto = new ItemDto();
        itemDto.setRequestId(999L);

        when(userService.findUserById(1L)).thenReturn(ownerDto);
        when(itemRequestStorage.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.create(itemDto, 1L));
        assertTrue(exception.getMessage().contains("Запрос с id=999 не найден"));
    }


    @Test
    void update_shouldUpdateItemSuccessfully() {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated");
        updateDto.setDescription("New desc");
        updateDto.setAvailable(false);

        when(itemStorage.findById(10L)).thenReturn(Optional.of(item));
        when(itemStorage.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.update(10L, updateDto, 1L);

        assertEquals("Updated", result.getName());
        assertEquals("New desc", result.getDescription());
        assertFalse(result.getAvailable());
    }

    @Test
    void update_shouldThrowNotFound_ifItemNotFound() {
        when(itemStorage.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.update(999L, new ItemDto(), 1L));
        assertTrue(exception.getMessage().contains("Вещь с id=999 не найдена"));
    }

    @Test
    void update_shouldThrowNotFound_ifOwnerMismatch() {
        when(itemStorage.findById(10L)).thenReturn(Optional.of(item));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.update(10L, new ItemDto(), 2L));
        assertEquals("Нельзя обновлять чужую вещь", exception.getMessage());
    }


    @Test
    void findItemById_withOwner_shouldIncludeBookingsAndComments() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        Item item = new Item();
        item.setId(10L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);

        User author = new User();
        author.setId(2L);
        author.setName("Author");
        author.setEmail("author@example.com");

        Comment comment = new Comment();
        comment.setText("Great!");
        comment.setAuthor(author);
        comment.setItem(item);

        when(itemStorage.findById(10L)).thenReturn(Optional.of(item));
        when(commentStorage.findByItemId(10L)).thenReturn(List.of(comment));

        User booker = new User();
        booker.setId(3L);
        booker.setName("Booker");
        booker.setEmail("booker@example.com");

        Booking lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setBooker(booker);
        lastBooking.setItem(item);

        Booking nextBooking = new Booking();
        nextBooking.setId(2L);
        nextBooking.setBooker(booker);
        nextBooking.setItem(item);

        when(bookingStorage.findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(
                eq(10L), any(LocalDateTime.class), eq(StatusOfBooking.APPROVED)))
                .thenReturn(lastBooking);
        when(bookingStorage.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                eq(10L), any(LocalDateTime.class), eq(StatusOfBooking.APPROVED)))
                .thenReturn(nextBooking);

        ItemDto result = itemService.findItemById(10L, 1L);

        assertNotNull(result);
        assertEquals(1, result.getComments().size());
        assertNotNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());
    }

    @Test
    void findItemById_withoutOwner_shouldNotIncludeBookings() {
        when(itemStorage.findById(10L)).thenReturn(Optional.of(item));
        when(commentStorage.findByItemId(10L)).thenReturn(Collections.emptyList());

        ItemDto result = itemService.findItemById(10L, 2L);

        assertNotNull(result);
        assertEquals(0, result.getComments().size());
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    @Test
    void findItemById_public_shouldIncludeBookingsAndComments() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        Item item = new Item();
        item.setId(10L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);

        User author = new User();
        author.setId(2L);
        author.setName("Author");
        author.setEmail("author@example.com");

        Comment comment = new Comment();
        comment.setText("Great!");
        comment.setAuthor(author);
        comment.setItem(item);

        when(itemStorage.findById(10L)).thenReturn(Optional.of(item));
        when(commentStorage.findByItemId(10L)).thenReturn(List.of(comment));

        User booker = new User();
        booker.setId(3L);
        booker.setName("Booker");
        booker.setEmail("booker@example.com");

        Booking lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setBooker(booker);
        lastBooking.setItem(item);

        Booking nextBooking = new Booking();
        nextBooking.setId(2L);
        nextBooking.setBooker(booker);
        nextBooking.setItem(item);

        when(bookingStorage.findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(
                eq(10L), any(LocalDateTime.class), eq(StatusOfBooking.APPROVED)))
                .thenReturn(lastBooking);
        when(bookingStorage.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                eq(10L), any(LocalDateTime.class), eq(StatusOfBooking.APPROVED)))
                .thenReturn(nextBooking);

        ItemDto result = itemService.findItemById(10L);

        assertNotNull(result);
        assertEquals(1, result.getComments().size());
        assertNotNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());
    }


    @Test
    void getAllItemsFromUser_shouldReturnItemsWithCommentsAndBookings() {
        when(itemStorage.findAllByOwnerId(1L)).thenReturn(List.of(item));
        when(commentStorage.findByItemId(10L)).thenReturn(Collections.emptyList());

        when(bookingStorage.findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(
                eq(10L), any(LocalDateTime.class), eq(StatusOfBooking.APPROVED)))
                .thenReturn(null);
        when(bookingStorage.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                eq(10L), any(LocalDateTime.class), eq(StatusOfBooking.APPROVED)))
                .thenReturn(null);

        List<ItemDto> result = itemService.getAllItemsFromUser(1L);

        assertEquals(1, result.size());
        assertEquals("Item", result.get(0).getName());
    }


    @Test
    void itemSearch_shouldReturnEmptyList_forBlankText() {
        List<ItemDto> result = itemService.itemSearch("   ");
        assertTrue(result.isEmpty());
    }

    @Test
    void itemSearch_shouldReturnMatchingItems() {
        Item foundItem = new Item();
        foundItem.setId(20L);
        foundItem.setName("Drill");
        foundItem.setDescription("Power drill");
        foundItem.setAvailable(true);
        foundItem.setOwner(owner);

        when(itemStorage.itemSearch("drill")).thenReturn(List.of(foundItem));

        List<ItemDto> result = itemService.itemSearch("drill");

        assertEquals(1, result.size());
        assertEquals("Drill", result.get(0).getName());
    }


    @Test
    void addComment_shouldAddCommentSuccessfully() {
        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setName("User");
        userDto.setEmail("user@example.com");

        User user = new User();
        user.setId(2L);
        user.setName("User");
        user.setEmail("user@example.com");

        when(userService.findUserById(2L)).thenReturn(userDto);
        when(itemStorage.findById(10L)).thenReturn(Optional.of(item));
        when(bookingStorage.existsByBookerIdAndItemIdAndEndBeforeAndStatus(
                eq(2L), eq(10L), any(LocalDateTime.class), eq(StatusOfBooking.APPROVED)))
                .thenReturn(true);

        Comment savedComment = new Comment();
        savedComment.setId(100L);
        savedComment.setText("Good!");
        savedComment.setAuthor(user);
        savedComment.setItem(item);
        savedComment.setCreated(LocalDateTime.now());

        when(commentStorage.save(any(Comment.class))).thenReturn(savedComment);

        CommentDto result = itemService.addComment(2L, 10L, "Good!");

        assertNotNull(result);
        assertEquals("Good!", result.getText());
    }

    @Test
    void addComment_shouldThrow_ifUserNeverBookedItem() {
        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setName("User");
        userDto.setEmail("user@example.com");

        when(userService.findUserById(2L)).thenReturn(userDto);
        when(itemStorage.findById(10L)).thenReturn(Optional.of(item));
        when(bookingStorage.existsByBookerIdAndItemIdAndEndBeforeAndStatus(
                eq(2L), eq(10L), any(LocalDateTime.class), eq(StatusOfBooking.APPROVED)))
                .thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> itemService.addComment(2L, 10L, "Comment"));
        assertEquals("Только пользователи, арендовавшие вещь, могут оставлять комментарии", exception.getMessage());
    }
}