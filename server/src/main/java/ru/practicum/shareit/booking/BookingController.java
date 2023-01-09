package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public BookingResponseDto add(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody BookingDto booking) {
        log.info("Добавление бронирования {},{}",userId, booking);
        return bookingService.add(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto patch(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId,
                                    @RequestParam Boolean approved) {
        log.info("Изменение бронирования {},{}",userId, bookingId);
        return bookingService.updateStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long bookingId) {
        log.info("Получение бронирования {},{}",userId, bookingId);
        return bookingService.getByAuthorOrOwner(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getListByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(defaultValue = "ALL") State state,
                                                  @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                  @RequestParam(value = "size", defaultValue = "10000") Integer size) {
        log.info("Получение по  ID пользователя {},{}",userId, state);
        return bookingService.getSort(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getListByItemOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                       @RequestParam(defaultValue = "ALL") State state,
                                                       @RequestParam(value = "from", defaultValue = "0")  Integer from,
                                                       @RequestParam(value = "size", defaultValue = "10000") Integer size) {
        log.info("Получение по владельцу {},{}, {}, {}",ownerId, state, from, size);
        return bookingService.getByItemOwner(ownerId, state, from, size);
    }
}