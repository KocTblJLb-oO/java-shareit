package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;


    // Создание бронирования
    @PostMapping
    public ResponseEntity<Object> createBooking(@Valid @RequestBody BookingDto bookingDto,
                                                @RequestHeader("X-Sharer-User-Id") Long booker) {
        log.info("Метод: createBooking. {}, {}", bookingDto, booker);
        return bookingClient.bookItem(bookingDto, booker);
    }

    // Подтверждение бронирования
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @PathVariable Long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Метод: approveBooking. bookingId {}, approved {}, ownerId {}", bookingId, approved, ownerId);

        return bookingClient.approveBooking(bookingId, approved, ownerId);
    }

    // Получение бронирования
    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Метод: getBookingById. bookingId {}, userId {}", bookingId, userId);
        return bookingClient.getBooking(bookingId, userId);
    }

    // Список бронирований текущего пользователя
    @GetMapping
    public ResponseEntity<Object> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        log.info("Метод: getUserBookings. userId {}, state {}", userId, state);
        return bookingClient.getBookings(userId, state);
    }
}
