package ru.practicum.shareit.jsonserialization;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;

@JsonTest
class BookingSerializationTests {

    @Autowired
    private JacksonTester<BookingResponseDto> jacksonTester;
    @Autowired
    private JacksonTester<Booking> jacksonTesterBooking;
    private BookingResponseDto bookingDto;
    private Booking booking;


    @BeforeEach
    void setUp() {
        BookingResponseDto.Item item = new BookingResponseDto.Item(1L, "name");

        bookingDto = new BookingResponseDto();
        bookingDto.setId(1L);
        bookingDto.setItem(item);
        bookingDto.setStart(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        bookingDto.setEnd(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS));

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        booking.setEnd(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    void itemDtoSerializationTest() throws IOException {
        JsonContent<BookingResponseDto> json = jacksonTester.write(bookingDto);
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
            assertThat(json).extractingJsonPathStringValue("$.start").isEqualTo(bookingDto.getStart().toString());
            assertThat(json).extractingJsonPathStringValue("$.end").isEqualTo(bookingDto.getEnd().toString());
            assertThat(json).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        });
    }

    @Test
    void itemDtoDeserializationTest() throws IOException {
        JsonContent<BookingResponseDto> json = jacksonTester.write(bookingDto);
        BookingResponseDto deserializedBookingDto = jacksonTester.parseObject(json.getJson());

        assertSoftly(softAssertions ->
                softAssertions.assertThat(deserializedBookingDto)
                        .usingRecursiveComparison()
                        .isEqualTo(bookingDto));
    }

    @Test
    void bookingDtoDeserializationTest() throws IOException {
        JsonContent<Booking> json = jacksonTesterBooking.write(booking);
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
            assertThat(json).extractingJsonPathStringValue("$.start").isEqualTo(booking.getStart().toString());
            assertThat(json).extractingJsonPathStringValue("$.end").isEqualTo(booking.getEnd().toString());
        });
    }
}
