package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ValidationException extends RuntimeException {

    public ValidationException(final String message) {
        super(message);
    }

}
