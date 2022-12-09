package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;


    @Transactional
    public BookingResponseDto addBooking(long userId, BookingDto booking) {
        if (!itemRepository.existsById(booking.getItemId())) {
            throw new BookingExceptionNotFound("Бронорование не доступно");
        }
        if (!userRepository.existsById(userId) || userId == 0) {
            throw new NoUserException("Пользователь не найден");
        }
        if (!itemRepository.existsById(booking.getItemId()) ||
                itemRepository.getById(booking.getItemId()).getUser() == userId) {
            throw new NoItemUserException("Вещь не найдена");
        }
        if (!itemRepository.getById(booking.getItemId()).getAvailable()) {
            throw new NotAvaliableException("Вещь не доступна");
        }
        if (booking.getStart().isBefore(LocalDateTime.now()) || booking.getEnd().isBefore(LocalDateTime.now()) ||
                booking.getEnd().isBefore(booking.getStart())) {
            throw new BookingException("Дата старта не может быть в прошлом");
        }
        return BookingMapper.bookingToBookingResponseDto(bookingRepository.save(BookingMapper.bookingDtoToBooking(booking,
                itemRepository.getById(booking.getItemId()), userRepository.getById(userId))));
    }

    public BookingResponseDto getBookingByAuthorOrOwner(long authorId, long bookingId) {
        if (!userRepository.existsById(authorId)) {
            throw new NotFoundException("Такого пользователя не существует");
        }
        if (authorId != bookingRepository.getById(bookingId).getItem().getUser()
                && authorId != bookingRepository.getById(bookingId).getBooker().getId()) {
            throw new NotFoundException("В доступе отказано");
        }
        return BookingMapper.bookingToBookingResponseDto(bookingRepository.getById(bookingId));
    }

    public List<BookingResponseDto> getBookingsSort(long authorId, State state) {
        if (!userRepository.existsById(authorId)) {
            throw new NotFoundException("Такого пользователя не существует");
        }
        LocalDateTime dateTime = LocalDateTime.now();
        switch (state.toString()) {
            case "ALL":
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByBooker_IdOrderByStartDesc(authorId));
            case "CURRENT":
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(authorId, dateTime, dateTime));
            case "FUTURE":
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByBooker_IdAndStartIsAfterOrderByStartDesc(authorId, dateTime));
            case "PAST":
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(authorId, dateTime));
            case "WAITING":
            case "REJECTED":
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByBooker_IdAndStatusContainingOrderByStartDesc(authorId, state.toString()));
            default:
                throw new BookingException("Unknown state: " + state);
        }
    }

    public List<BookingResponseDto> getBookingsByItemOwner(long ownerId, State state) throws NotFoundException {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Такого пользователя не существует");
        }
        LocalDateTime dateTime = LocalDateTime.now();
        switch (state.toString()) {
            case "ALL":
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByItem_UserOrderByStartDesc(ownerId));
            case "CURRENT":
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByItem_UserAndStartIsBeforeAndEndIsAfterOrderByStartDesc(ownerId, dateTime, dateTime));
            case "FUTURE":
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByItem_UserAndStartIsAfterOrderByStartDesc(ownerId, dateTime));
            case "PAST":
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByItem_UserAndEndIsBeforeOrderByStartDesc(ownerId, dateTime));
            case "WAITING":
            case "REJECTED":
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByItem_UserAndStatusContainingOrderByStartDesc(ownerId, state.toString()));
            default:
                throw new BookingException("Unknown state: " + state.toString());
        }
    }

    @Transactional
    public BookingResponseDto updateStatusBooking(long authorId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.getById(bookingId);
        if (booking.getItem().getUser() != authorId) {
            throw new NotFoundException("Вещь не найдена");
        }
        if (!booking.getStatus().equals("WAITING")) {
            throw new BookingException("Статус запроса уже изменен");
        }
        if (approved) {
            booking.setStatus("APPROVED");
        } else {
            booking.setStatus("REJECTED");
        }
        return BookingMapper.bookingToBookingResponseDto(bookingRepository.save(booking));
    }
}