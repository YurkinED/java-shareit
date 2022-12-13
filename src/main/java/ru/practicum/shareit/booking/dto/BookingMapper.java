package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class BookingMapper {

    public static BookingDto bookingToBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(booking.getItem().getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        return bookingDto;
    }

    public static List<BookingDto> bookingsToBookingDtoList(List<Booking> bookingList) {
        return bookingList.stream().map(BookingMapper::bookingToBookingDto).collect(Collectors.toList());
    }

    public static Booking bookingDtoToBooking(BookingDto bookingDto, Item item, User user) {
        Booking booking = new Booking();
        booking.setEnd(bookingDto.getEnd());
        booking.setStart(bookingDto.getStart());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    public static BookingResponseDto bookingToBookingResponseDto(Booking booking) {
        BookingResponseDto bookingDto = new BookingResponseDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setBooker(new BookingResponseDto.User(booking.getBooker().getId(), booking.getBooker().getName()));
        bookingDto.setItem(new BookingResponseDto.Item(booking.getItem().getId(), booking.getItem().getName()));
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    public static List<BookingResponseDto> bookingsToBookingResponseDtoList(List<Booking> bookingList) {
        return bookingList.stream().map(BookingMapper::bookingToBookingResponseDto).collect(Collectors.toList());
    }
}
