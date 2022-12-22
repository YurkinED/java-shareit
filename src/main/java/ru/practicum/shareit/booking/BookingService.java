package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
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
        if (!booking.getStart().isBefore(booking.getEnd())) {
            throw new BookingException("Дата старта не может быть позже или равна окончанию");
        }
        Item item = itemRepository.findById(booking.getItemId()).orElseThrow(() -> {
            throw new NotFoundException("Бронирование не найдено");
        });
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь не найден");
        });
        if (!item.getAvailable()) {
            throw new BookingException("Вещь не доступна");
        }
        if (item.getUser() != null) {
            if (item.getUser().getId() == userId) {
                throw new NotFoundException("Вещь не найдена " + item.getUser().getId() + " " + userId);
            }
        }
        return BookingMapper.bookingToBookingResponseDto(
                bookingRepository.save(BookingMapper.bookingDtoToBooking(booking, item, user)));
    }

    public BookingResponseDto getByAuthorOrOwner(long authorId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException("Бронивароние не найдено");
        });
        if (authorId != booking.getItem().getUser().getId() && authorId != booking.getBooker().getId()) {
            throw new NotFoundException("В доступе отказано");
        }
        return BookingMapper.bookingToBookingResponseDto(booking);
    }

    public List<BookingResponseDto> getSort(long authorId, State state, Integer from, Integer size) {
        if (!userRepository.existsById(authorId)) {
            throw new NotFoundException("Такого пользователя не существует");
        }
        LocalDateTime dateTime = LocalDateTime.now();
        Pageable pageable;
        if (from != null && size != null) {
            pageable = PageRequest.of(from / size, size, sortStartDesc);
        } else {
            pageable = PageRequest.of(0, Integer.MAX_VALUE, sortStartDesc);
        }
        switch (state) {
            case ALL:
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByBooker_IdOrderByStartDesc(authorId, pageable));
            case CURRENT:
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(authorId, dateTime, dateTime, pageable));
            case FUTURE:
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByBooker_IdAndStartIsAfterOrderByStartDesc(authorId, dateTime, pageable));
            case PAST:
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByBooker_IdAndEndIsBefore(authorId, dateTime, pageable));
            case WAITING:
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByBooker_IdAndStatusEquals(authorId, BookingStatus.WAITING, pageable));
            case REJECTED:
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByBooker_IdAndStatusEquals(authorId, BookingStatus.REJECTED, pageable));
            default:
                throw new BookingException("Unknown state: " + state);
        }
    }

    public List<BookingResponseDto> getByItemOwner(long ownerId, State state, Integer from, Integer size) throws NotFoundException {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Такого пользователя не существует");
        }
        LocalDateTime dateTime = LocalDateTime.now();
        Pageable pageable;
        if (from != null && size != null) {
            pageable = PageRequest.of(from / size, size, sortStartDesc);
        } else {
            pageable = PageRequest.of(0, Integer.MAX_VALUE, sortStartDesc);
        }
        switch (state) {
            case ALL:
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByItem_UserId(ownerId, pageable));
            case CURRENT:
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByItem_UserIdAndStartIsBeforeAndEndIsAfter(ownerId, dateTime, dateTime, pageable));
            case FUTURE:
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByItem_UserIdAndStartIsAfter(ownerId, dateTime, pageable));
            case PAST:
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByItem_UserIdAndEndIsBefore(ownerId, dateTime, pageable));
            case WAITING:
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByItem_UserIdAndStatusEquals(ownerId, BookingStatus.WAITING, pageable));
            case REJECTED:
                return BookingMapper.bookingsToBookingResponseDtoList(bookingRepository
                        .findAllByItem_UserIdAndStatusEquals(ownerId, BookingStatus.REJECTED, pageable));
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