package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {
    BookingDtoResponse create(@Valid BookingDto bookingDto, Long booker);

    BookingDtoResponse approveBooking(Long bookingId, Boolean approved, Long ownerId);

    BookingDtoResponse getBookingById(Long bookingId, Long userId);

    List<BookingDtoResponse> getUserBookings(Long userId, String state);
}
