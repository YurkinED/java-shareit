package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
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
    private final Sort sortStartDesc = Sort.by(Sort.Direction.DESC, "start");

    @Transactional
    public BookingResponseDto add(long userId, BookingDto booking) {
        if (!itemRepository.existsById(booking.getItemId())) {
            throw new BookingExceptionNotFound("Бронорование не доступно");
        }
        if (!userRepository.existsById(userId) || userId == 0) {
            throw new NoUserException("Пользователь не найден");
        }
        if (!itemRepository.existsById(booking.getItemId()) ||
                itemRepository.findById(booking.getItemId()).orElseThrow().getUser().getId() == userId) {
            throw new NoItemUserException("Вещь не найдена");
        }
        if (!itemRepository.findById(booking.getItemId()).orElseThrow().getAvailable()) {
            throw new NotAvaliableException("Вещь не доступна");
        }
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().isEqual(booking.getStart())) {
            throw new BookingException("Дата старта не может быть позже или равна окончанию");
        }
        return BookingMapper.bookingToBookingResponseDto(bookingRepository.save(BookingMapper.bookingDtoToBooking(booking,
                itemRepository.findById(booking.getItemId()).orElseThrow(), userRepository.getById(userId))));
    }

    public BookingResponseDto getByAuthorOrOwner(long authorId, long bookingId) {
        if (!userRepository.existsById(authorId)) {
            throw new NotFoundException("Такого пользователя не существует");
        }
        if (authorId != bookingRepository.findById(bookingId).orElseThrow().getItem().getUser().getId()
                && authorId != bookingRepository.findById(bookingId).orElseThrow().getBooker().getId()) {
            throw new NotFoundException("В доступе отказано");
        }
        return BookingMapper.bookingToBookingResponseDto(bookingRepository.getById(bookingId));
    }

    public List<BookingResponseDto> getSort(long authorId, State state) {
        if (!userRepository.existsById(authorId)) {
            throw new NotFoundException("Такого пользователя не существует");
        }
        LocalDateTime dateTime = LocalDateTime.now();
        switch (state) {
            case ALL:
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByBooker_IdOrderByStartDesc(authorId));
            case CURRENT:
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(authorId, dateTime, dateTime, sortStartDesc));
            case FUTURE:
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByBooker_IdAndStartIsAfterOrderByStartDesc(authorId, dateTime, sortStartDesc));
            case PAST:
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByBooker_IdAndEndIsBefore(authorId, dateTime, sortStartDesc));
            case WAITING:
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByBooker_IdAndStatusEquals(authorId, BookingStatus.WAITING, sortStartDesc));
            case REJECTED:
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByBooker_IdAndStatusEquals(authorId, BookingStatus.REJECTED, sortStartDesc));
            default:
                throw new BookingException("Unknown state: " + state);
        }
    }

    public List<BookingResponseDto> getByItemOwner(long ownerId, State state) throws NotFoundException {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Такого пользователя не существует");
        }
        LocalDateTime dateTime = LocalDateTime.now();
        switch (state) {
            case ALL:
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByItem_UserId(ownerId,Sort.by(Sort.Direction.DESC, "start")));
            case CURRENT:
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByItem_UserIdAndStartIsBeforeAndEndIsAfter(ownerId, dateTime, dateTime, sortStartDesc));
            case FUTURE:
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByItem_UserIdAndStartIsAfter(ownerId, dateTime, sortStartDesc));
            case PAST:
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByItem_UserIdAndEndIsBefore(ownerId, dateTime, sortStartDesc));
            case WAITING:
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByItem_UserIdAndStatusEquals(ownerId, BookingStatus.WAITING, sortStartDesc));
            case REJECTED:
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByItem_UserIdAndStatusEquals(ownerId, BookingStatus.REJECTED, sortStartDesc));
            default:
                throw new BookingException("Unknown state: " + state.toString());
        }
    }

    @Transactional
    public BookingResponseDto updateStatus(long authorId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.getById(bookingId);
        if (booking.getItem().getUser().getId() != authorId) {
            throw new NotFoundException("Вещь не найдена");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BookingException("Статус запроса уже изменен");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.bookingToBookingResponseDto(booking);
    }
}