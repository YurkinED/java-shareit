package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker_IdOrderByStartDesc(long userId);

    List<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long userId, LocalDateTime dateTime,
                                                                                  LocalDateTime dateTime1);

    List<Booking> findAllByBooker_IdAndStartIsAfterOrderByStartDesc(long userId, LocalDateTime dateTime);

    List<Booking> findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(long userId, LocalDateTime dateTime);

    List<Booking> findAllByBooker_IdAndStatusContainingOrderByStartDesc(long userId, String status);

    List<Booking> findAllByItem_UserOrderByStartDesc(long ownerId);

    List<Booking> findAllByItem_UserAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long ownerId,
                                                                                  LocalDateTime dateTime,
                                                                                  LocalDateTime dateTime1);

    List<Booking> findAllByItem_UserAndStartIsAfterOrderByStartDesc(long ownerId, LocalDateTime dateTime);

    List<Booking> findAllByItem_UserAndEndIsBeforeOrderByStartDesc(long ownerId, LocalDateTime dateTime);

    List<Booking> findAllByItem_UserAndStatusContainingOrderByStartDesc(long ownerId, String state);

    List<Booking> findAllByItem_IdAndBooker_IdAndEndIsBefore(long itemId, long userId, LocalDateTime dateTime);

    List<Booking> findAllByItem_IdAndItem_User(long id, long userId);


}
