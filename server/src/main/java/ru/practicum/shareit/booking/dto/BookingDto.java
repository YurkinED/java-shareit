package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private long itemId;
    private LocalDateTime start;
    private LocalDateTime end;

    public BookingDto() {

    }
}