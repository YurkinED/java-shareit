package ru.practicum.shareit.unittests;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

@ExtendWith(MockitoExtension.class)
class BookingServiceTests {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    Long bookerId;
    long bookerId2 = 1L;
    Long bookingId;
    BookingDto bookingRequestDto;
    User user;
    Item item;
    Booking booking2;
    BookingDto bookingDto;
    BookingResponseDto booking;
    BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(bookingRepository, itemRepository, userRepository);
        bookerId = 2L;
        bookingId = 1L;
        bookingRequestDto = new BookingDto();
        bookingRequestDto.setItemId(bookingId);
        bookingRequestDto.setStart(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        bookingRequestDto.setEnd(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS));

        user = new User(1L, "testUserName", "testUser@email.com");
        item = new Item();
        item.setId(1L);
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setRequestId(1L);
        item.setUser(user);

        booking2 = BookingMapper.bookingDtoToBooking(bookingRequestDto, item, user);
        bookingDto = BookingMapper.bookingToBookingDto(booking2);
        booking = BookingMapper.bookingToBookingResponseDto(booking2);
    }

    @Test
    void bookItemTest() {

        Mockito.when(userRepository.findById(bookerId)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(any())).thenReturn(booking2);
        BookingResponseDto actualBooking = bookingService.add(bookerId, bookingDto);
        assertSoftly(softAssertions ->
                softAssertions.assertThat(actualBooking)
                        .usingRecursiveComparison()
                        .isEqualTo(booking));
    }


    @Test
    void decidingOnRequestTest() {
        item.getUser().setId(2L);
        booking2 = BookingMapper.bookingDtoToBooking(bookingRequestDto, item, user);
        bookingDto = BookingMapper.bookingToBookingDto(booking2);
        booking = BookingMapper.bookingToBookingResponseDto(booking2);
        Mockito.when(bookingRepository.getById(any())).thenReturn(booking2);

        var actualBooking = bookingService.updateStatus(bookerId, bookingId, true);
        assertSoftly(softAssertions ->
                softAssertions.assertThat(actualBooking)
                        .usingRecursiveComparison()
                        .ignoringFields("status")
                        .isEqualTo(booking));
    }

    @Test
    void getByAuthorOrOwnerTest() {
        item.getUser().setId(2L);
        booking2 = BookingMapper.bookingDtoToBooking(bookingRequestDto, item, user);
        bookingDto = BookingMapper.bookingToBookingDto(booking2);
        booking = BookingMapper.bookingToBookingResponseDto(booking2);
        Mockito.when(bookingRepository.findById(any())).thenReturn(Optional.ofNullable(booking2));

        var actualBooking = bookingService.getByAuthorOrOwner(bookerId, bookingId);
        assertSoftly(softAssertions ->
                softAssertions.assertThat(actualBooking)
                        .usingRecursiveComparison()
                        .ignoringFields("status")
                        .isEqualTo(booking));
    }

    @Test
    void getSortTest() {
        item.getUser().setId(2L);
        booking2 = BookingMapper.bookingDtoToBooking(bookingRequestDto, item, user);
        bookingDto = BookingMapper.bookingToBookingDto(booking2);
        booking = BookingMapper.bookingToBookingResponseDto(booking2);
        Mockito.when(userRepository.existsById(any())).thenReturn(true);
        Mockito.when(bookingRepository.findAllByBooker_IdOrderByStartDesc(anyLong(), any())).thenReturn(List.of(booking2));

        var actualBooking = bookingService.getSort(bookerId, State.ALL, 0, 1000);
        assertSoftly(softAssertions ->
                softAssertions.assertThat(actualBooking)
                        .usingRecursiveComparison()
                        .ignoringFields("status")
                        .isEqualTo(List.of(booking)));
    }

    @Test
    void getByItemOwnerTest() {
        item.getUser().setId(2L);
        booking2 = BookingMapper.bookingDtoToBooking(bookingRequestDto, item, user);
        bookingDto = BookingMapper.bookingToBookingDto(booking2);
        booking = BookingMapper.bookingToBookingResponseDto(booking2);
        Mockito.when(userRepository.existsById(any())).thenReturn(true);
        Mockito.when(bookingRepository.findAllByItem_UserId(anyLong(), any())).thenReturn(List.of(booking2));

        var actualBooking = bookingService.getByItemOwner(bookerId, State.ALL, 0, 1000);
        assertSoftly(softAssertions ->
                softAssertions.assertThat(actualBooking)
                        .usingRecursiveComparison()
                        .ignoringFields("status")
                        .isEqualTo(List.of(booking)));
    }
}
