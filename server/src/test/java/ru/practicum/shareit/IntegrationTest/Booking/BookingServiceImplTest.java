package ru.practicum.shareit.IntegrationTest.Booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import jakarta.validation.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.other.StatusOfBooking;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingStorage bookingStorage;

    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User owner;
    private User booker;
    private Item item;
    private BookingDto bookingDto;
    private Booking booking;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("Имя");
        owner.setEmail("owner@ya.ru");

        booker = new User();
        booker.setId(2L);
        booker.setName("Booker");
        booker.setEmail("booker@ya.ru");

        itemRequest = new ItemRequest();
        itemRequest.setId(5L);
        itemRequest.setRequester(booker);

        item = new Item();
        item.setId(10L);
        item.setName("Вещь");
        item.setDescription("Описание");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(itemRequest);

        bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));
        bookingDto.setItemId(item.getId());
        bookingDto.setBooker(booker.getId());

        booking = new Booking();
        booking.setId(100L);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(StatusOfBooking.WAITING);
    }

    private void mockCommonServices() {
        when(userService.findUserById(booker.getId())).thenReturn(toUserDto(booker));
        when(userService.findUserById(owner.getId())).thenReturn(toUserDto(owner));
        when(itemService.findItemById(item.getId())).thenReturn(toItemDto(item));
        when(itemService.findItemById(eq(item.getId()), anyLong())).thenReturn(toItemDto(item));

        if (item.getRequest() != null) {
            when(itemRequestService.getItemRequestById(item.getRequest().getId()))
                    .thenReturn(new ru.practicum.shareit.request.dto.ItemRequestDto(
                            item.getRequest().getId(),
                            "Нужна вещь",
                            booker.getId(),
                            LocalDateTime.now(),
                            Collections.emptyList()
                    ));
        }
    }

    @Test
    void create_shouldCreateBookingSuccessfully_withItemRequest() {
        mockCommonServices();
        when(bookingStorage.save(any(Booking.class))).thenReturn(booking);

        BookingDtoResponse result = bookingService.create(bookingDto, booker.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(StatusOfBooking.WAITING, result.getStatus());
    }

    @Test
    void create_shouldCreateBookingSuccessfully_withoutItemRequest() {
        item.setRequest(null);
        mockCommonServices();
        when(bookingStorage.save(any(Booking.class))).thenReturn(booking);

        BookingDtoResponse result = bookingService.create(bookingDto, booker.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void create_shouldThrowValidationException_ifItemNotAvailable() {
        item.setAvailable(false);
        mockCommonServices();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.create(bookingDto, booker.getId()));
        assertEquals("Вещь недоступна для бронирования", exception.getMessage());
    }

    @Test
    void create_shouldThrowValidationException_ifBookerIsOwner() {
        bookingDto.setBooker(owner.getId());
        mockCommonServices();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.create(bookingDto, owner.getId()));
        assertEquals("Нельзя забронировать свою вещь", exception.getMessage());
    }

    @Test
    void create_shouldThrowValidationException_ifStartAfterEnd() {
        bookingDto.setStart(LocalDateTime.now().plusHours(2));
        bookingDto.setEnd(LocalDateTime.now().plusHours(1));
        mockCommonServices();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.create(bookingDto, booker.getId()));
        assertEquals("Дата начала должна быть раньше даты окончания", exception.getMessage());
    }

    @Test
    void approveBooking_shouldApproveBooking() {
        when(bookingStorage.findById(100L)).thenReturn(Optional.of(booking));
        when(userService.findUserById(booker.getId())).thenReturn(toUserDto(booker));
        when(bookingStorage.save(any(Booking.class))).thenReturn(booking);

        BookingDtoResponse result = bookingService.approveBooking(100L, true, owner.getId());
        assertEquals(StatusOfBooking.APPROVED, result.getStatus());
    }

    @Test
    void approveBooking_shouldRejectBooking() {
        when(bookingStorage.findById(100L)).thenReturn(Optional.of(booking));
        when(userService.findUserById(booker.getId())).thenReturn(toUserDto(booker));
        when(bookingStorage.save(any(Booking.class))).thenReturn(booking);

        BookingDtoResponse result = bookingService.approveBooking(100L, false, owner.getId());
        assertEquals(StatusOfBooking.REJECTED, result.getStatus());
    }

    @Test
    void approveBooking_shouldThrow_ifNotOwner() {
        when(bookingStorage.findById(100L)).thenReturn(Optional.of(booking));
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.approveBooking(100L, true, 999L));
        assertEquals("Только владелец может подтверждать бронирование", ex.getMessage());
    }

    @Test
    void approveBooking_shouldThrow_ifAlreadyProcessed() {
        booking.setStatus(StatusOfBooking.APPROVED);
        when(bookingStorage.findById(100L)).thenReturn(Optional.of(booking));
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.approveBooking(100L, true, owner.getId()));
        assertEquals("Нельзя изменить статус бронирования, если оно уже подтверждено или отклонено", ex.getMessage());
    }

    @Test
    void getBookingById_shouldReturn_forBooker() {
        when(bookingStorage.findById(100L)).thenReturn(Optional.of(booking));
        BookingDtoResponse result = bookingService.getBookingById(100L, booker.getId());
        assertNotNull(result);
    }

    @Test
    void getBookingById_shouldReturn_forOwner() {
        when(bookingStorage.findById(100L)).thenReturn(Optional.of(booking));
        BookingDtoResponse result = bookingService.getBookingById(100L, owner.getId());
        assertNotNull(result);
    }

    @Test
    void getBookingById_shouldThrow_forUnauthorizedUser() {
        when(bookingStorage.findById(100L)).thenReturn(Optional.of(booking));
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.getBookingById(100L, 999L));
        assertEquals("Недостаточно прав для просмотра бронирования", ex.getMessage());
    }

    @Test
    void getUserBookings_shouldReturnAll() {
        when(bookingStorage.findByBookerIdOrderByStartDesc(booker.getId())).thenReturn(List.of(booking));
        List<BookingDtoResponse> result = bookingService.getUserBookings(booker.getId(), "ALL");
        assertEquals(1, result.size());
    }

    @Test
    void getUserBookings_shouldReturnCurrent() {
        LocalDateTime now = LocalDateTime.of(2025, 10, 28, 10, 0);
        booking.setStart(now.minusHours(1));
        booking.setEnd(now.plusHours(1));

        // Используем any() для времени, чтобы избежать расхождений
        when(bookingStorage.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                eq(booker.getId()), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> result = bookingService.getUserBookings(booker.getId(), "CURRENT");
        assertEquals(1, result.size());
    }

    @Test
    void getUserBookings_shouldThrow_onInvalidState() {
        // Enum.valueOf выбрасывает IllegalArgumentException, а не IllegalStateException
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.getUserBookings(booker.getId(), "INVALID"));
        assertTrue(ex.getMessage().contains("No enum constant"));
    }

    private UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    private ItemDto toItemDto(Item item) {
        Long requestId = (item.getRequest() != null) ? item.getRequest().getId() : null;
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                requestId
        );
    }
}