package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker_IdOrderByStartDesc(long userId, Pageable page);

    List<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(long userId, LocalDateTime dateTime,
                                                                  LocalDateTime dateTime1, Pageable page);

    List<Booking> findAllByBooker_IdAndStartIsAfterOrderByStartDesc(long userId, LocalDateTime dateTime, Pageable page);


    List<Booking> findAllByBooker_IdAndEndIsBefore(long userId, LocalDateTime dateTime, Pageable page);

    List<Booking> findAllByBooker_IdAndStatusEquals(long userId, BookingStatus status, Pageable page);

    List<Booking> findAllByItem_UserId(long ownerId, Pageable page);

    List<Booking> findAllByItem_UserIdAndStartIsBeforeAndEndIsAfter(long ownerId,
                                                                    LocalDateTime dateTime,
                                                                    LocalDateTime dateTime1,
                                                                    Pageable page);

    List<Booking> findAllByItem_UserIdAndStartIsAfter(long ownerId, LocalDateTime dateTime, Pageable page);

    List<Booking> findAllByItem_UserIdAndEndIsBefore(long ownerId, LocalDateTime dateTime, Pageable page);


    List<Booking> findAllByItem_UserIdAndStatusEquals(long ownerId, BookingStatus state, Pageable page);

    List<Booking> findAllByItem_IdAndBooker_IdAndEndIsBefore(long itemId, long userId, LocalDateTime dateTime);

    List<Booking> findAllByItem_IdAndItem_User_Id(long id, long userId);

    List<Booking> findByItemInAndStatusEquals(List<Item> items, BookingStatus stat);


}
