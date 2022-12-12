package ru.practicum.shareit.exceptions;

public class BookingExceptionNotFound extends RuntimeException {
    public BookingExceptionNotFound(String message) {
        super(message);
    }
}