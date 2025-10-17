package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.other.StatusOfBooking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingStorage extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, StatusOfBooking status);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long bookerId, LocalDateTime startBefore, LocalDateTime endAfter);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime startAfter);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime endBefore);

    boolean existsByBookerIdAndItemIdAndEndBeforeAndStatus(Long bookerId, Long itemId, LocalDateTime endBefore, StatusOfBooking status);

    Booking findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(long id, LocalDateTime now, StatusOfBooking statusOfBooking);

    Booking findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(long id, LocalDateTime now, StatusOfBooking statusOfBooking);
}
