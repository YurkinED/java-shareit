package ru.practicum.shareit.unittests;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

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

    @Test
    void bookItemTest() {
        var bookingService = new BookingService(bookingRepository, itemRepository, userRepository);

        var bookerId = 2L;
        var bookingRequestDto = new BookingDto();
        bookingRequestDto.setItemId(1L);
        bookingRequestDto.setStart(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        bookingRequestDto.setEnd(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS));

        var user = new User(1L, "testUserName", "testUser@email.com");
        var item = new Item();
        item.setId(1L);
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setRequestId(1L);
        item.setUser(user);

        var booking2 = BookingMapper.bookingDtoToBooking(bookingRequestDto, item, user);
        var bookingDto = BookingMapper.bookingToBookingDto(booking2);
        var booking = BookingMapper.bookingToBookingResponseDto(booking2);

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
        var bookingService = new BookingService(bookingRepository, itemRepository, userRepository);
        var bookerId = 2L;
        var bookingRequestDto = new BookingDto();
        var bookingId = 1L;
        bookingRequestDto.setItemId(bookingId);
        bookingRequestDto.setStart(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        bookingRequestDto.setEnd(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS));

        var user = new User(bookerId, "testUserName", "testUser@email.com");
        var item = new Item();
        item.setId(1L);
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setRequestId(1L);
        item.setUser(user);

        var booking2 = BookingMapper.bookingDtoToBooking(bookingRequestDto, item, user);
        var booking = BookingMapper.bookingToBookingResponseDto(booking2);

        Mockito.when(bookingRepository.getById(any())).thenReturn(booking2);

        var actualBooking = bookingService.updateStatus(bookerId, bookingId, true);
        assertSoftly(softAssertions ->
                softAssertions.assertThat(actualBooking)
                        .usingRecursiveComparison()
                        .ignoringFields("status")
                        .isEqualTo(booking));
    }
/*
  @Test
  void getBookingInfoTest() {
    var bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);

    var userId = 1L;
    var bookingRequestDto = BookingCreateRequestDto.builder()
        .id(1L)
        .itemId(1L)
        .startDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
        .endDateTime(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS))
        .build();
    var booking = BookingMapper.toBooking(userId, bookingRequestDto);
    booking.setItem(Item.builder()
        .id(1L)
        .name("itemName")
        .description("itemDescription")
        .isAvailable(true)
        .requestId(1L)
        .ownerId(userId)
        .build());
    booking.setBooker(new User(userId, "testUserName", "testUser@email.com"));

    Mockito.when(bookingRepository.findById(bookingRequestDto.getId())).thenReturn(Optional.of(booking));

    var actualBooking = bookingService.getBookingInfo(userId, bookingRequestDto.getId());
    assertSoftly(softAssertions ->
        softAssertions.assertThat(actualBooking)
            .usingRecursiveComparison()
            .isEqualTo(BookingMapper.toBookingCreateResponseDto(booking)));
  }

  @Test
  void getAllBookingInfoTest() {
    var bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);

    var userId = 1L;
    var bookingRequestDto = BookingCreateRequestDto.builder()
        .id(1L)
        .itemId(1L)
        .startDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
        .endDateTime(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS))
        .build();
    var booking = BookingMapper.toBooking(userId, bookingRequestDto);
    booking.setItem(Item.builder()
        .id(1L)
        .name("itemName")
        .description("itemDescription")
        .isAvailable(true)
        .requestId(1L)
        .ownerId(userId)
        .build());
    booking.setBooker(new User(userId, "testUserName", "testUser@email.com"));

    Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(booking.getBooker()));
    Mockito.when(bookingRepository.findAllByBookerId(anyLong(), any())).thenReturn(List.of(booking));

    var actualBooking = bookingService.getAllBookingInfo(userId, BookingFilter.ALL, null, null);
    assertSoftly(softAssertions ->
        softAssertions.assertThat(actualBooking)
            .usingRecursiveComparison()
            .isEqualTo(List.of(BookingMapper.toBookingCreateResponseDto(booking))));
  }

  @Test
  void getAllOwnerBookingInfoTest() {
    var bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);

    var userId = 1L;
    var bookingRequestDto = BookingCreateRequestDto.builder()
        .id(1L)
        .itemId(1L)
        .startDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
        .endDateTime(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS))
        .build();
    var booking = BookingMapper.toBooking(userId, bookingRequestDto);
    booking.setItem(Item.builder()
        .id(1L)
        .name("itemName")
        .description("itemDescription")
        .isAvailable(true)
        .requestId(1L)
        .ownerId(userId)
        .build());
    booking.setBooker(new User(userId, "testUserName", "testUser@email.com"));

    Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(booking.getBooker()));
    Mockito.when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(List.of(booking.getItem()));
    Mockito.when(bookingRepository.findAllByItemIdIn(anyList(), any())).thenReturn(List.of(booking));

    var actualBooking = bookingService.getAllOwnerBookingInfo(userId, BookingFilter.ALL, null, null);
    assertSoftly(softAssertions ->
        softAssertions.assertThat(actualBooking)
            .usingRecursiveComparison()
            .isEqualTo(List.of(BookingMapper.toBookingCreateResponseDto(booking))));
  }

   */
}
