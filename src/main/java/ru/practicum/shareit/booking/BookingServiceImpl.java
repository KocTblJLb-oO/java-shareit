package ru.practicum.shareit.booking;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.other.StatusBookingOfUser;
import ru.practicum.shareit.other.StatusOfBooking;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingStorage bookingStorage;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    @Transactional
    public BookingDtoResponse create(BookingDto bookingDto, Long bookerId) {
        log.info("Метод: create. {}, {}", bookingDto, bookerId);

        User booker = UserMapper.toUser(userService.findUserById(bookerId));
        User owner = UserMapper.toUser(userService.findUserById(bookingDto.getItemId()));
        Item item = ItemMapper.toItem(itemService.findItemById(bookingDto.getItemId(), owner.getId()), owner);

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }
        if (item.getOwner().getId() == bookerId) {
            throw new ValidationException("Нельзя забронировать свою вещь");
        }
        if (!bookingDto.getStart().isBefore(bookingDto.getEnd())) {
            throw new ValidationException("Дата начала должна быть раньше даты окончания");
        }

        bookingDto.setStatus(StatusOfBooking.WAITING);

        BookingDtoResponse booking = BookingMapper.toBookingDtoResponse(bookingStorage.save(BookingMapper.toBooking(bookingDto, item, booker)), item, booker);
        log.debug("Метод: create. Созданное бронирование - {}", booking);

        return booking;
    }

    // Подтверждение бронирования
    @Transactional
    public BookingDtoResponse approveBooking(Long bookingId, Boolean approved, Long ownerId) {
        log.info("Метод: approveBooking. bookingId {}, approved {}, ownerId {}", bookingId, approved, ownerId);
        if (approved == null) {
            throw new IllegalArgumentException("Параметр 'approved' не может быть null");
        }

        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Бронирование с ID " + bookingId + " не найдено"));

        Item item = booking.getItem();
        if (!(item.getOwner().getId() == ownerId)) {
            throw new RuntimeException("Только владелец может подтверждать бронирование");
        }

        if (booking.getStatus() != StatusOfBooking.WAITING) {
            throw new RuntimeException("Нельзя изменить статус бронирования, если оно уже подтверждено или отклонено");
        }

        StatusOfBooking newStatus = approved ? StatusOfBooking.APPROVED : StatusOfBooking.REJECTED;
        booking.setStatus(newStatus);
        Booking savedBooking = bookingStorage.save(booking);

        User booker = UserMapper.toUser(userService.findUserById(booking.getBooker().getId()));

        return BookingMapper.toBookingDtoResponse(savedBooking, item, booker);
    }

    // Получение бронирования
    public BookingDtoResponse getBookingById(Long bookingId, Long userId) {
        log.info("Метод: getBookingById. bookingId {}, userId {}", bookingId, userId);

        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Бронирование с ID " + bookingId + " не найдено"));

        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();

        if (!userId.equals(bookerId) && !userId.equals(ownerId)) {
            throw new RuntimeException("Недостаточно прав для просмотра бронирования");
        }

        Item item = booking.getItem();
        User booker = booking.getBooker();

        return BookingMapper.toBookingDtoResponse(booking, item, booker);
    }

    // Список бронирований текущего пользователя
    public List<BookingDtoResponse> getUserBookings(Long userId, String state) {
        log.info("Метод: getUserBookings. userId {}, state {}", userId, state);

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        StatusBookingOfUser statusBookingOfUser = StatusBookingOfUser.valueOf(state.toUpperCase());

        bookings = switch (statusBookingOfUser) {
            case ALL -> bookingStorage.findByBookerIdOrderByStartDesc(userId);
            case WAITING -> bookingStorage.findByBookerIdAndStatusOrderByStartDesc(
                    userId, StatusOfBooking.WAITING);
            case REJECTED -> bookingStorage.findByBookerIdAndStatusOrderByStartDesc(
                    userId, StatusOfBooking.REJECTED);
            case CURRENT -> bookingStorage.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                    userId, now, now);
            case FUTURE -> bookingStorage.findByBookerIdAndStartAfterOrderByStartDesc(
                    userId, now);
            case PAST -> bookingStorage.findByBookerIdAndEndBeforeOrderByStartDesc(
                    userId, now);
            default -> throw new IllegalStateException("Некорректный статус: " + state);
        };

        return bookings.stream()
                .map(this::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    private BookingDtoResponse toBookingDtoResponse(Booking booking) {
        Item item = booking.getItem();
        User booker = booking.getBooker();
        return BookingMapper.toBookingDtoResponse(booking, item, booker);
    }
}
