package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<String> validationException(final ValidationException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<String> noUserException(NoUserException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> noItemUserException(NoItemUserException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
