package ru.practicum.shareit.exceptions;

public class NoItemUserException extends RuntimeException {
    public NoItemUserException(final String message) {
        super(message);
    }
}
