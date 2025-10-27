package ru.practicum.shareit.other;

public enum StatusOfBooking {
    WAITING, // — новое бронирование, ожидает одобрения
    APPROVED, //  — бронирование подтверждено владельцем
    REJECTED, // — бронирование отклонено владельцем
    CANCELED // — бронирование отменено создателем
}
