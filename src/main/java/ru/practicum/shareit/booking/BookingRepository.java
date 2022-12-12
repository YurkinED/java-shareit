package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker_IdOrderByStartDesc(long userId);

    List<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(long userId, LocalDateTime dateTime,
                                                                  LocalDateTime dateTime1, Sort sort);

    List<Booking> findAllByBooker_IdAndStartIsAfterOrderByStartDesc(long userId, LocalDateTime dateTime, Sort sort);

    List<Booking> findAllByBooker_IdAndEndIsBefore(long userId, LocalDateTime dateTime, Sort sort);

    List<Booking> findAllByBooker_IdAndStatusEquals(long userId, BookingStatus status, Sort sort);

    List<Booking> findAllByItem_UserId(long ownerId, Sort sort);

    List<Booking> findAllByItem_UserIdAndStartIsBeforeAndEndIsAfter(long ownerId,
                                                                    LocalDateTime dateTime,
                                                                    LocalDateTime dateTime1,
                                                                    Sort sort);

    List<Booking> findAllByItem_UserIdAndStartIsAfter(long ownerId, LocalDateTime dateTime, Sort sort);

    List<Booking> findAllByItem_UserIdAndEndIsBefore(long ownerId, LocalDateTime dateTime, Sort sort);


    List<Booking> findAllByItem_UserIdAndStatusEquals(long ownerId, BookingStatus state, Sort sort);

    List<Booking> findAllByItem_IdAndBooker_IdAndEndIsBefore(long itemId, long userId, LocalDateTime dateTime);

    List<Booking> findAllByItem_IdAndItem_User_Id(long id, long userId);

    List<Booking> findByItemIdAndEndIsBefore(long ownerId, LocalDateTime dateTime, Sort sort);


}
