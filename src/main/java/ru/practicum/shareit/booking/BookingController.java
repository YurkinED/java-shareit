package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto add(@RequestHeader("X-Sharer-User-Id") long userId, @Validated @RequestBody BookingDto booking) {
        log.info("Добавление бронирования {},{}",userId, booking);
        return bookingService.add(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto patch(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId,
                                    @RequestParam Boolean approved) {
        return bookingService.updateStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long bookingId) {
        return bookingService.getByAuthorOrOwner(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getListByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(defaultValue = "ALL") State state) {
        log.info("Получение по  ID {},{}",userId, state);
        return bookingService.getSort(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getListByItemOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                       @RequestParam(defaultValue = "ALL") State state) {
        return bookingService.getByItemOwner(ownerId, state);
    }
}