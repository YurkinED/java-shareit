package ru.practicum.shareit.integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.exceptions.BookingException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.mapper.MapToItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWithDateBooking;
import ru.practicum.shareit.user.model.User;


@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingTests {

    private final EntityManager em;
    private final BookingService bookingService;
    private final ItemService itemService;


    @Test
    void addBookingTest() {
        var user = new User(0, "authorName", "mail@mail.com");
        var user2 = new User(0, "authorName1", "mail1@mail.com");

        em.persist(user);
        em.persist(user2);
        var item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setUser(user);
        em.persist(item);
        em.flush();
        var booking = new Booking();
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(10));
        booking.setEnd(LocalDateTime.now().minusDays(5));


        BookingResponseDto bookingResponseDto = BookingMapper.bookingToBookingResponseDto(booking);

        var targetItem = bookingService.add(user2.getId(), BookingMapper.bookingToBookingDto(booking));
        assertSoftly(softAssertions ->
                softAssertions.assertThat(targetItem)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "status", "booker")
                        .isEqualTo(bookingResponseDto));
    }

    @Test
    void getByAuthorOrOwnerTest() {
        var user = new User(0, "authorName", "mail@mail.com");
        var user2 = new User(0, "authorName1", "mail1@mail.com");

        em.persist(user);
        em.persist(user2);
        var item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setUser(user);
        em.persist(item);

        var booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(10));
        booking.setEnd(LocalDateTime.now().minusDays(5));
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);
        em.flush();

        BookingResponseDto bookingResponseDto = BookingMapper.bookingToBookingResponseDto(booking);


        var targetItem = bookingService.getByAuthorOrOwner(user.getId(), booking.getId());
        assertSoftly(softAssertions ->
                softAssertions.assertThat(targetItem)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "status", "booker")
                        .isEqualTo(bookingResponseDto));
    }

    @Test
    void getByAuthorOrOwnerExceptionTest() {
        var user = new User(0, "authorName", "mail@mail.com");
        var user2 = new User(0, "authorName1", "mail1@mail.com");

        em.persist(user);
        em.persist(user2);
        var item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setUser(user);
        em.persist(item);

        var booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(10));
        booking.setEnd(LocalDateTime.now().minusDays(5));
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);
        em.flush();

        BookingResponseDto bookingResponseDto = BookingMapper.bookingToBookingResponseDto(booking);

        assertThatThrownBy(() -> {
            bookingService.getByAuthorOrOwner(100L, 100L);
        }).isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Бронирование не найдено");

        assertThatThrownBy(() -> {
            bookingService.getByAuthorOrOwner(100L, booking.getId());
        }).isInstanceOf(NotFoundException.class)
                .hasMessageContaining("В доступе отказано");
    }

    @Test
    void getSortExceptionTest() {
        var user = new User(0, "authorName", "mail@mail.com");
        var user2 = new User(0, "authorName1", "mail1@mail.com");

        em.persist(user);
        em.persist(user2);
        var item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setUser(user);
        em.persist(item);

        var booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(10));
        booking.setEnd(LocalDateTime.now().plusDays(5));
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);
        em.flush();

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setCreated(LocalDateTime.now());
        comment.setText("text");
        comment.setAuthor(user);

        ItemWithDateBooking itemWithDateBooking = MapToItem.itemToItemWithDateBookingDto(item, List.of(booking), List.of(comment));


        BookingResponseDto bookingResponseDto = BookingMapper.bookingToBookingResponseDto(booking);
        var responseBooking = List.of(bookingResponseDto);


        assertThatThrownBy(() -> {
            bookingService.getSort(100L, State.ALL, 0, 1000);
        }).isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Такого пользователя не существует");

        assertThatThrownBy(() -> {
            bookingService.getSort(user.getId(), State.valueOf("unknownstate"), 0, 1000);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No enum constant ru.practicum.shareit.booking.model.State.unknownstate");
    }


    @Test
    void getByItemOwnerAllTest() {
        var user = new User(0, "authorName", "mail@mail.com");
        var user2 = new User(0, "authorName1", "mail1@mail.com");

        em.persist(user);
        em.persist(user2);
        var item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setUser(user);
        em.persist(item);

        var booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(10));
        booking.setEnd(LocalDateTime.now().plusDays(5));
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);
        em.flush();

        BookingResponseDto bookingResponseDto = BookingMapper.bookingToBookingResponseDto(booking);
        var responseBooking = List.of(bookingResponseDto);


        var targetItem = bookingService.getByItemOwner(user.getId(), State.ALL, 0, 1000);
        assertSoftly(softAssertions ->
                softAssertions.assertThat(targetItem)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "status", "booker")
                        .isEqualTo(responseBooking));

        var targetItemCurrent = bookingService.getByItemOwner(user.getId(), State.CURRENT, 0, 1000);
        assertSoftly(softAssertions ->
                softAssertions.assertThat(targetItemCurrent)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "status", "booker")
                        .isEqualTo(responseBooking));

        var targetItemPast = bookingService.getByItemOwner(user.getId(), State.PAST, 0, 1000);
        assertSoftly(softAssertions ->
                softAssertions.assertThat(targetItemCurrent)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "status", "booker")
                        .isEqualTo(responseBooking));

        var targetItemWaiting = bookingService.getByItemOwner(user.getId(), State.WAITING, 0, 1000);
        assertSoftly(softAssertions ->
                softAssertions.assertThat(targetItemWaiting)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "status", "booker")
                        .isEqualTo(responseBooking));

        var booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setItem(item);
        booking2.setStart(LocalDateTime.now().plusDays(10));
        booking2.setEnd(LocalDateTime.now().plusDays(11));
        booking2.setStatus(BookingStatus.REJECTED);
        em.persist(booking2);
        em.flush();
        BookingResponseDto bookingResponseDto2 = BookingMapper.bookingToBookingResponseDto(booking2);

        var responseBookingFuture = List.of(bookingResponseDto2);

        var targetItemFuture = bookingService.getByItemOwner(user.getId(), State.FUTURE, 0, 1000);
        assertSoftly(softAssertions ->
                softAssertions.assertThat(targetItemFuture)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "status", "booker")
                        .isEqualTo(responseBookingFuture));
        var targetItemRejected = bookingService.getByItemOwner(user.getId(), State.REJECTED, 0, 1000);
        assertSoftly(softAssertions ->
                softAssertions.assertThat(targetItemRejected)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "status", "booker")
                        .isEqualTo(responseBookingFuture));

        var targetItemRejectedPaged = bookingService.getByItemOwner(user.getId(), State.REJECTED, 0, 1);
        assertSoftly(softAssertions ->
                softAssertions.assertThat(targetItemRejectedPaged)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "status", "booker")
                        .isEqualTo(responseBookingFuture));


    }

    @Test
    void itemWithDateBookingTest() {
        var user = new User(0, "authorName", "mail@mail.com");
        var user2 = new User(0, "authorName1", "mail1@mail.com");

        em.persist(user);
        em.persist(user2);
        var item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setUser(user);
        em.persist(item);

        var booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(10));
        booking.setEnd(LocalDateTime.now().plusDays(5));
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);
        em.flush();

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setCreated(LocalDateTime.now());
        comment.setText("text");
        comment.setAuthor(user);
    }

    @Test
    void getSortTest() {
        var user = new User(0, "authorName", "mail@mail.com");
        var user2 = new User(0, "authorName1", "mail1@mail.com");

        em.persist(user);
        em.persist(user2);
        var item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setUser(user);
        em.persist(item);

        var booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(10));
        booking.setEnd(LocalDateTime.now().plusDays(5));
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);
        em.flush();

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setCreated(LocalDateTime.now());
        comment.setText("text");
        comment.setAuthor(user);

        ItemWithDateBooking itemWithDateBooking = MapToItem.itemToItemWithDateBookingDto(item, List.of(booking), List.of(comment));


        BookingResponseDto bookingResponseDto = BookingMapper.bookingToBookingResponseDto(booking);
        var responseBooking = List.of(bookingResponseDto);


        var targetItem = bookingService.getSort(user.getId(), State.ALL, 0, 1000);
        assertSoftly(softAssertions ->
                softAssertions.assertThat(targetItem)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "status", "booker")
                        .isEqualTo(responseBooking));
        var targetItemCurrent = bookingService.getSort(user.getId(), State.CURRENT, 0, 1000);
        assertSoftly(softAssertions ->
                softAssertions.assertThat(targetItemCurrent)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "status", "booker")
                        .isEqualTo(responseBooking));


        var targetItemWaiting = bookingService.getSort(user.getId(), State.WAITING, 0, 1000);
        assertSoftly(softAssertions ->
                softAssertions.assertThat(targetItemWaiting)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "status", "booker")
                        .isEqualTo(responseBooking));


        var booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setItem(item);
        booking2.setStart(LocalDateTime.now().plusDays(1));
        booking2.setEnd(LocalDateTime.now().plusDays(5));
        booking.setStatus(BookingStatus.REJECTED);
        em.persist(booking2);
        em.flush();

        BookingResponseDto bookingResponseDto2 = BookingMapper.bookingToBookingResponseDto(booking2);
        var responseBooking2 = List.of(bookingResponseDto2);


        var targetItemFuture = bookingService.getSort(user.getId(), State.FUTURE, 0, 1000);
        assertSoftly(softAssertions ->
                softAssertions.assertThat(targetItemFuture)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "status", "booker")
                        .isEqualTo(responseBooking2));

        var targetItemRejected = bookingService.getSort(user.getId(), State.REJECTED, 0, 1000);
        assertSoftly(softAssertions ->
                softAssertions.assertThat(targetItemRejected)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "status", "booker")
                        .isEqualTo(responseBooking));

        var booking3 = new Booking();
        booking3.setBooker(user);
        booking3.setItem(item);
        booking3.setStart(LocalDateTime.now().minusDays(5));
        booking3.setEnd(LocalDateTime.now().minusDays(1));
        em.persist(booking3);
        em.flush();

        BookingResponseDto bookingResponseDto3 = BookingMapper.bookingToBookingResponseDto(booking3);
        var responseBooking3 = List.of(bookingResponseDto3);

        var targetItemPast = bookingService.getSort(user.getId(), State.PAST, 0, 1000);
        assertSoftly(softAssertions ->
                softAssertions.assertThat(targetItemPast)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "status", "booker")
                        .isEqualTo(responseBooking3));
        ItemWithDateBooking itemWithDateBooking2 = MapToItem.itemToItemWithDateBookingDto(item, List.of(booking, booking2, booking3), List.of(comment));

    }

    @Test
    void updateStatusTest() {
        var user = new User(0, "authorName", "mail@mail.com");
        var user2 = new User(0, "authorName1", "mail1@mail.com");

        em.persist(user);
        em.persist(user2);
        var item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setUser(user);
        em.persist(item);

        var booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(10));
        booking.setEnd(LocalDateTime.now().minusDays(5));
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);
        em.flush();

        BookingResponseDto bookingResponseDto = BookingMapper.bookingToBookingResponseDto(booking);
        bookingResponseDto.setStatus(BookingStatus.APPROVED);
        var responseBooking = bookingResponseDto;


        var targetItem = bookingService.updateStatus(user.getId(), booking.getId(), true);
        assertSoftly(softAssertions ->
                softAssertions.assertThat(targetItem)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "booker")
                        .isEqualTo(responseBooking));


    }

    @Test
    void updateStatus2Test() {
        var user = new User(0, "authorName", "mail@mail.com");
        var user2 = new User(0, "authorName1", "mail1@mail.com");

        em.persist(user);
        em.persist(user2);
        var item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setUser(user);
        em.persist(item);

        var booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(10));
        booking.setEnd(LocalDateTime.now().minusDays(5));
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);
        em.flush();

        BookingResponseDto bookingResponseDto = BookingMapper.bookingToBookingResponseDto(booking);
        bookingResponseDto.setStatus(BookingStatus.REJECTED);
        var responseBooking = bookingResponseDto;


        var targetItem = bookingService.updateStatus(user.getId(), booking.getId(), false);
        assertSoftly(softAssertions ->
                softAssertions.assertThat(targetItem)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "booker")
                        .isEqualTo(responseBooking));
    }


    @Test
    void updateStatusWaitingTest() {
        var user = new User(0, "authorName", "mail@mail.com");
        var user2 = new User(0, "authorName1", "mail1@mail.com");

        em.persist(user);
        em.persist(user2);
        var item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setUser(user);
        em.persist(item);

        var booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(10));
        booking.setEnd(LocalDateTime.now().minusDays(5));
        booking.setStatus(BookingStatus.APPROVED);
        em.persist(booking);
        em.flush();

        BookingResponseDto bookingResponseDto = BookingMapper.bookingToBookingResponseDto(booking);
        bookingResponseDto.setStatus(BookingStatus.WAITING);

        assertThatThrownBy(() -> {
            bookingService.updateStatus(user.getId(), booking.getId(), true);
        }).isInstanceOf(BookingException.class)
                .hasMessageContaining("Статус запроса уже изменен");
    }


    @Test
    void getSortException2Test() {
        var user = new User(0, "authorName", "mail@mail.com");

        em.persist(user);
        var item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setUser(user);
        em.persist(item);

        var booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(10));
        booking.setEnd(LocalDateTime.now().plusDays(5));
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);
        em.flush();
        BookingResponseDto bookingResponseDto = BookingMapper.bookingToBookingResponseDto(booking);
        assertThatThrownBy(() -> {
            bookingService.getSort(user.getId(), State.valueOf("ERROR"), null, null);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No enum constant ru.practicum.shareit.booking.model.State.ERROR");
    }


    @Test
    void getByItemOwnerExceptionTest() {
        var user = new User(0, "authorName", "mail@mail.com");
        var user2 = new User(0, "authorName1", "mail1@mail.com");

        em.persist(user);
        em.persist(user2);
        var item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setUser(user);
        em.persist(item);

        var booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(10));
        booking.setEnd(LocalDateTime.now().plusDays(5));
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);
        em.flush();

        BookingResponseDto bookingResponseDto = BookingMapper.bookingToBookingResponseDto(booking);
        assertThatThrownBy(() -> {
            bookingService.getByItemOwner(1000L, State.ALL, null, null);
        }).isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Такого пользователя не существует");

    }

    @Test
    void updateStatusExceptionTest() {
        var user = new User(0, "authorName", "mail@mail.com");
        var user2 = new User(0, "authorName1", "mail1@mail.com");

        em.persist(user);
        em.persist(user2);
        var item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setUser(user);
        em.persist(item);

        var booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(10));
        booking.setEnd(LocalDateTime.now().minusDays(5));
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);
        em.flush();

        BookingResponseDto bookingResponseDto = BookingMapper.bookingToBookingResponseDto(booking);
        bookingResponseDto.setStatus(BookingStatus.APPROVED);
        assertThatThrownBy(() -> {
            bookingService.updateStatus(user2.getId(), booking.getId(), true);
        }).isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Вещь не найдена");

    }

    @Test
    void addBookingExceptionTest() {
        var user2 = new User(0, "authorName1", "mail1@mail.com");
        var item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setUser(user2);

        var booking = new Booking();
        booking.setItem(item);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().minusDays(5));

        var booking2 = new Booking();
        booking2.setItem(item);
        booking2.setStart(LocalDateTime.now().minusDays(5));
        booking2.setEnd(LocalDateTime.now());


        BookingResponseDto bookingResponseDto = BookingMapper.bookingToBookingResponseDto(booking);

        assertThatThrownBy(() -> {
            bookingService.add(user2.getId(), BookingMapper.bookingToBookingDto(booking2));
        }).isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Бронирование не найдено");
    }

    @Test
    void addBookingException2Test() {
        var user1 = new User(0, "authorName1", "mail1@mail.com");
        var user2 = new User(0, "authorName2", "mail2@mail.com");
        em.persist(user1);
        var item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setUser(user1);
        em.persist(item);
        em.flush();


        var booking = new Booking();
        booking.setItem(item);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(5));

        assertThatThrownBy(() -> {
            bookingService.add(user2.getId(), BookingMapper.bookingToBookingDto(booking));
        }).isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }
}

