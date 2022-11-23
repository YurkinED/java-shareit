package ru.practicum.shareit.exceptions;

public class NoUserException extends RuntimeException {
    public NoUserException(final String message) {
        super(message);
    }
}
