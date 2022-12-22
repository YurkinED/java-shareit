package ru.practicum.shareit.jpa;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@DataJpaTest
class BookingJpaTests {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TestEntityManager em;

    User user;
    User user2;
    Item item;
    Item item2;

    Booking booking;
    Booking booking2;


    LocalDateTime startDate;
    LocalDateTime startDate2;
    LocalDateTime endDate;
    LocalDateTime endDate2;


    @BeforeEach
    void setUp() {
        user = new User(0, "testUserName", "testUser@email.com");
        user2 = new User(0, "testUserName2", "testUser2@email.com");
        item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setUser(user);
        item2 = new Item();
        item2.setName("itemName");
        item2.setDescription("itemDescription");
        item2.setAvailable(true);
        item2.setUser(user2);
        booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().minusDays(10).truncatedTo(ChronoUnit.SECONDS));
        booking.setEnd(LocalDateTime.now().plusDays(10).truncatedTo(ChronoUnit.SECONDS));
        booking.setStatus(BookingStatus.WAITING);
        booking2 = new Booking();
        booking2.setItem(item2);
        booking2.setBooker(user2);
        booking2.setStart(LocalDateTime.now().minusDays(10).truncatedTo(ChronoUnit.SECONDS));
        booking2.setEnd(LocalDateTime.now().plusDays(10).truncatedTo(ChronoUnit.SECONDS));
        booking2.setStatus(BookingStatus.REJECTED);
        startDate = LocalDateTime.now().minusHours(1);
        endDate = LocalDateTime.now().plusHours(1);
        startDate2 = LocalDateTime.now().minusDays(11);
        endDate2 = LocalDateTime.now().plusDays(11);

    }

    @Test
    void findAllByBooker_IdOrderByStartDescTest() {
        em.persist(user);
        em.persist(item);
        em.persist(booking);

        var bookings = bookingRepository.findAllByBooker_IdOrderByStartDesc(user.getId(), Pageable.unpaged());

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(bookings.size())
                    .usingRecursiveComparison()
                    .isEqualTo(1);

            softAssertions.assertThat(bookings.get(0))
                    .usingRecursiveComparison()
                    .isEqualTo(booking);
        });
    }

    @Test
    void findAllByBooker_IdAndStartIsBeforeAndEndIsAfterTest() {
        em.persist(user);
        em.persist(item);
        em.persist(booking);

        var bookings = bookingRepository.findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(user.getId(), startDate, endDate, Pageable.unpaged());

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(bookings.size())
                    .usingRecursiveComparison()
                    .isEqualTo(1);

            softAssertions.assertThat(bookings.get(0))
                    .usingRecursiveComparison()
                    .isEqualTo(booking);
        });
    }

    @Test
    void findAllByBooker_IdAndStartIsAfterOrderByStartDescTest() {
        em.persist(user);
        em.persist(item);
        em.persist(booking);

        var bookings = bookingRepository.findAllByBooker_IdAndStartIsAfterOrderByStartDesc(user.getId(), startDate2, Pageable.unpaged());

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(bookings.size())
                    .usingRecursiveComparison()
                    .isEqualTo(1);

            softAssertions.assertThat(bookings.get(0))
                    .usingRecursiveComparison()
                    .isEqualTo(booking);
        });
    }


    @Test
    void findAllByBooker_IdAndEndIsBeforeTest() {
        em.persist(user);
        em.persist(item);
        em.persist(booking);

        var bookings = bookingRepository.findAllByBooker_IdAndEndIsBefore(user.getId(), endDate2, Pageable.unpaged());

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(bookings.size())
                    .usingRecursiveComparison()
                    .isEqualTo(1);

            softAssertions.assertThat(bookings.get(0))
                    .usingRecursiveComparison()
                    .isEqualTo(booking);
        });
    }

    @Test
    void findAllByBooker_IdAndStatusEqualsTest() {
        em.persist(user);
        em.persist(item);
        em.persist(user2);
        em.persist(item2);
        em.persist(booking);
        em.persist(booking2);
        em.flush();

        var bookings = bookingRepository.findAllByBooker_IdAndStatusEquals(user.getId(), BookingStatus.WAITING, Pageable.unpaged());

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(bookings.size())
                    .usingRecursiveComparison()
                    .isEqualTo(1);

            softAssertions.assertThat(bookings.get(0))
                    .usingRecursiveComparison()
                    .isEqualTo(booking);
        });
    }

    @Test
    void findAllByItem_UserIdTest() {
        em.persist(user);
        em.persist(item);
        em.persist(booking);

        var bookings = bookingRepository.findAllByItem_UserId(user.getId(), Pageable.unpaged());

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(bookings.size())
                    .usingRecursiveComparison()
                    .isEqualTo(1);

            softAssertions.assertThat(bookings.get(0))
                    .usingRecursiveComparison()
                    .isEqualTo(booking);
        });
    }

    @Test
    void findAllByItem_UserIdAndStartIsBeforeAndEndIsAfterTest() {
        em.persist(user);
        em.persist(item);
        em.persist(booking);

        var bookings = bookingRepository.findAllByItem_UserIdAndStartIsBeforeAndEndIsAfter(user.getId(), startDate, endDate, Pageable.unpaged());

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(bookings.size())
                    .usingRecursiveComparison()
                    .isEqualTo(1);

            softAssertions.assertThat(bookings.get(0))
                    .usingRecursiveComparison()
                    .isEqualTo(booking);
        });
    }


    @Test
    void findAllByItem_UserIdAndStartIsAfterTest() {
        em.persist(user);
        em.persist(item);
        em.persist(booking);

        var bookings = bookingRepository.findAllByItem_UserIdAndStartIsAfter(user.getId(), startDate2, Pageable.unpaged());

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(bookings.size())
                    .usingRecursiveComparison()
                    .isEqualTo(1);

            softAssertions.assertThat(bookings.get(0))
                    .usingRecursiveComparison()
                    .isEqualTo(booking);
        });
    }

    @Test
    void findAllByItem_UserIdAndEndIsBefore() {
        em.persist(user);
        em.persist(item);
        em.persist(booking);

        var bookings = bookingRepository.findAllByItem_UserIdAndEndIsBefore(user.getId(), endDate2, Pageable.unpaged());

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(bookings.size())
                    .usingRecursiveComparison()
                    .isEqualTo(1);

            softAssertions.assertThat(bookings.get(0))
                    .usingRecursiveComparison()
                    .isEqualTo(booking);
        });
    }

    @Test
    void findAllByItem_UserIdAndStatusEqualsTest() {
        em.persist(user);
        em.persist(item);
        em.persist(user2);
        em.persist(item2);
        em.persist(booking);
        em.persist(booking2);
        var bookings = bookingRepository.findAllByItem_UserIdAndStatusEquals(user.getId(), BookingStatus.WAITING, Pageable.unpaged());
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(bookings.size())
                    .usingRecursiveComparison()
                    .isEqualTo(1);

            softAssertions.assertThat(bookings.get(0))
                    .usingRecursiveComparison()
                    .isEqualTo(booking);
        });
    }

    @Test
    void findAllByItem_IdAndBooker_IdAndEndIsBeforeTest() {
        em.persist(user);
        em.persist(item);
        em.persist(user2);
        em.persist(item2);
        em.persist(booking);
        em.persist(booking2);

        var bookings = bookingRepository.findAllByItem_IdAndBooker_IdAndEndIsBefore(item.getId(), user.getId(), endDate2);

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(bookings.size())
                    .usingRecursiveComparison()
                    .isEqualTo(1);

            softAssertions.assertThat(bookings.get(0))
                    .usingRecursiveComparison()
                    .isEqualTo(booking);
        });
    }

    @Test
    void findAllByItem_IdAndItem_User_IdTest() {
        em.persist(user);
        em.persist(item);
        em.persist(user2);
        em.persist(item2);
        em.persist(booking);
        em.persist(booking2);

        var bookings = bookingRepository.findAllByItem_IdAndItem_User_Id(item.getId(), user.getId());

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(bookings.size())
                    .usingRecursiveComparison()
                    .isEqualTo(1);

            softAssertions.assertThat(bookings.get(0))
                    .usingRecursiveComparison()
                    .isEqualTo(booking);
        });
    }

    @Test
    void findByItemInAndStatusEqualsTest() {
        em.persist(user);
        em.persist(item);
        em.persist(user2);
        em.persist(item2);
        em.persist(booking);
        em.persist(booking2);

        var bookings = bookingRepository.findByItemInAndStatusEquals(List.of(item), BookingStatus.WAITING);

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(bookings.size())
                    .usingRecursiveComparison()
                    .isEqualTo(1);

            softAssertions.assertThat(bookings.get(0))
                    .usingRecursiveComparison()
                    .isEqualTo(booking);
        });
    }

    @Test
    void findAllCurrentBookingsByItemsIdsTest() {
        em.persist(user);
        em.persist(item);
        em.persist(user2);
        em.persist(item2);
        em.persist(booking);

        var bookings = bookingRepository.findByItemInAndStatusEquals(List.of(item), BookingStatus.WAITING);

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(bookings.size())
                    .usingRecursiveComparison()
                    .isEqualTo(1);

            softAssertions.assertThat(bookings.get(0))
                    .usingRecursiveComparison()
                    .isEqualTo(booking);
        });
    }
}

