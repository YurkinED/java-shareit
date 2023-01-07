package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;


@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> validationException(final MethodArgumentTypeMismatchException ex) {
        log.error("MethodArgumentTypeMismatchException {}", ex.toString());
        Map<String, String> map = new HashMap<>();
        String message = "Unknown state: UNSUPPORTED_STATUS";
        map.put("error", message);
        return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler
    public ResponseEntity<String> noUserException(NotFoundException ex) {
        log.error("NotFoundException {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<String> noItemUserException(NoSuchElementException ex) {
        log.error("noItemUserException {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<String> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("methodArgumentNotValidException  {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> throwable(Throwable ex) {
        log.error("throwable " + ex.toString());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleEmailException(final EmailException e) {
        log.error("Исключение! EmailException: {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleBookingException(final BookingException e) {
        log.error("Исключение! BookingException: {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleEntityNotFoundException(final EntityNotFoundException e) {
        log.error("Исключение! EntityNotFoundException: {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }


}
