package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;


    // Создание бронирования
    @PostMapping
    public BookingDtoResponse createBooking(@Valid @RequestBody BookingDto bookingDto,
                                            @RequestHeader("X-Sharer-User-Id") Long booker) {
        log.info("Метод: createBooking. {}, {}", bookingDto, booker);
        return bookingService.create(bookingDto, booker);
    }

    // Подтверждение бронирования
    @PatchMapping("/{bookingId}")
    public BookingDtoResponse approveBooking(
            @PathVariable Long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Метод: approveBooking. bookingId {}, approved {}, ownerId {}", bookingId, approved, ownerId);

        return bookingService.approveBooking(bookingId, approved, ownerId);
    }

    // Получение бронирования
    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBookingById(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Метод: getBookingById. bookingId {}, userId {}", bookingId, userId);
        return bookingService.getBookingById(bookingId, userId);
    }

    // Список бронирований текущего пользователя
    @GetMapping
    public List<BookingDtoResponse> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        log.info("Метод: getUserBookings. userId {}, state {}", userId, state);
        return bookingService.getUserBookings(userId, state);
    }
}
