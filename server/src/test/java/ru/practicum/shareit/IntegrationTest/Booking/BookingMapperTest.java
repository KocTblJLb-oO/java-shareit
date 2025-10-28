package ru.practicum.shareit.IntegrationTest.Booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.other.StatusOfBooking;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    @Test
    void toBookingDto_shouldConvertBookingToDtoCorrectly() {
        User booker = new User();
        booker.setId(1L);
        booker.setEmail("user@ya.ru");
        booker.setName("Пользователь");

        Item item = new Item();
        item.setId(2L);
        item.setName("Вещь");
        item.setDescription("Описание");
        item.setAvailable(true);
        item.setOwner(booker);

        Booking booking = new Booking();
        booking.setId(100L);
        booking.setStart(LocalDateTime.of(2025, 10, 28, 10, 0));
        booking.setEnd(LocalDateTime.of(2025, 10, 30, 10, 0));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(StatusOfBooking.APPROVED);

        BookingDto dto = BookingMapper.toBookingDto(booking);

        assertEquals(100L, dto.getId());
        assertEquals(booking.getStart(), dto.getStart());
        assertEquals(booking.getEnd(), dto.getEnd());
        assertEquals(item.getId(), dto.getItemId());
        assertEquals(booker.getId(), dto.getBooker());
        assertEquals(StatusOfBooking.APPROVED, dto.getStatus());
    }

    @Test
    void toBooking_shouldConvertDtoToBookingCorrectly() {
        User booker = new User();
        booker.setId(1L);
        booker.setEmail("user@ya.ru");
        booker.setName("Пользователь");

        Item item = new Item();
        item.setId(2L);
        item.setName("Вещь");
        item.setDescription("Описание");
        item.setAvailable(true);
        item.setOwner(booker);

        BookingDto dto = new BookingDto();
        dto.setId(100L);
        dto.setStart(LocalDateTime.of(2025, 10, 28, 10, 0));
        dto.setEnd(LocalDateTime.of(2025, 10, 30, 10, 0));
        dto.setItemId(item.getId());
        dto.setBooker(booker.getId());
        dto.setStatus(StatusOfBooking.WAITING);

        Booking booking = BookingMapper.toBooking(dto, item, booker);

        assertEquals(dto.getId(), booking.getId());
        assertEquals(dto.getStart(), booking.getStart());
        assertEquals(dto.getEnd(), booking.getEnd());
        assertEquals(item, booking.getItem());
        assertEquals(booker, booking.getBooker());
        assertEquals(StatusOfBooking.WAITING, booking.getStatus());
    }

    @Test
    void toBookingDtoResponse_shouldConvertBookingToResponseDtoCorrectly() {
        User booker = new User();
        booker.setId(1L);
        booker.setEmail("user@ya.ru");
        booker.setName("Пользователь");

        Item item = new Item();
        item.setId(2L);
        item.setName("Вещь");
        item.setDescription("Описание");
        item.setAvailable(true);
        item.setOwner(booker);

        Booking booking = new Booking();
        booking.setId(100L);
        booking.setStart(LocalDateTime.of(2025, 10, 28, 10, 0));
        booking.setEnd(LocalDateTime.of(2025, 10, 30, 10, 0));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(StatusOfBooking.APPROVED);

        BookingDtoResponse response = BookingMapper.toBookingDtoResponse(booking, item, booker);

        assertEquals(100L, response.getId());
        assertEquals(booking.getStart(), response.getStart());
        assertEquals(booking.getEnd(), response.getEnd());
        assertNotNull(response.getItem());
        assertEquals(item.getId(), response.getItem().getId());
        assertNotNull(response.getBooker());
        assertEquals(booker.getId(), response.getBooker().getId());
        assertEquals(StatusOfBooking.APPROVED, response.getStatus());
    }
}