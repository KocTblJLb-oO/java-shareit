package ru.practicum.shareit.booking;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Booking {
    private Long id;
    LocalDate start;
    LocalDate end;
    long item;
    long booker;

}
