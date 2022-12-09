package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto addBooking(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody BookingDto booking) {
        log.info("Добавление бронирования {},{}",userId, booking);
        return bookingService.addBooking(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto patchBooking(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId,
                                           @RequestParam Boolean approved) {
        return bookingService.updateStatusBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long bookingId) {
        return bookingService.getBookingByAuthorOrOwner(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getListBookingByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(defaultValue = "ALL") State state) {
        return bookingService.getBookingsSort(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getListBookingByItemOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                              @RequestParam(defaultValue = "ALL") State state) {
        return bookingService.getBookingsByItemOwner(ownerId, state);
    }
}