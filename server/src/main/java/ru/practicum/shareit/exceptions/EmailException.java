package ru.practicum.shareit.exceptions;

import org.springframework.dao.DataIntegrityViolationException;

public class EmailException extends DataIntegrityViolationException {
    public EmailException(String message) {
        super(message);
    }
}

