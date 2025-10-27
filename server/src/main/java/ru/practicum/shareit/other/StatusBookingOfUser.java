package ru.practicum.shareit.other;

public enum StatusBookingOfUser {
    ALL,
    CURRENT, // текущие
    PAST, // завершённые
    FUTURE,
    WAITING, // ожидающие подтверждения
    REJECTED // отклонённые
}
