package ru.practicum.shareit.jsonserialization;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemWithDateBooking;

@JsonTest
class ItemSerializationTests {

    @Autowired
    private JacksonTester<ItemDto> jacksonTester;
    @Autowired
    private JacksonTester<ItemWithDateBooking> jacksonTester2;
    private ItemDto itemDto;

    private ItemWithDateBooking itemWithDateBooking;

    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .requestId(1L)
                .build();
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("text");
        comment.setCreated(LocalDateTime.now());

        itemWithDateBooking = new ItemWithDateBooking();
        itemWithDateBooking.setId(1L);
        itemWithDateBooking.setName("itemName");
        itemWithDateBooking.setDescription("itemDescription");
        itemWithDateBooking.setAvailable(true);
        itemWithDateBooking.setLastBooking(new ItemWithDateBooking.Booking(1L, 1L));
        itemWithDateBooking.setNextBooking(new ItemWithDateBooking.Booking(1L, 1L));
        itemWithDateBooking.setComments(List.of(new ItemWithDateBooking.Comment(comment.getId(), comment.getText(),
                "author", comment.getCreated())));
    }

    @Test
    void itemDtoSerializationTest() throws IOException {
        JsonContent<ItemDto> json = jacksonTester.write(itemDto);

        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
            assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("itemName");
            assertThat(json).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
            assertThat(json).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
        });
    }

    @Test
    void itemWithDateBookingSerializationTest() throws IOException {
        JsonContent<ItemWithDateBooking> json = jacksonTester2.write(itemWithDateBooking);
        System.out.println(json);
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
            assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("itemName");
            assertThat(json).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
            assertThat(json).extractingJsonPathStringValue("$.description").isEqualTo("itemDescription");
            //  assertThat(json).extractingJsonPathNumberValue("$.comments.id").isEqualTo(1);
            //  assertThat(json).extractingJsonPathStringValue("$.comments.text").isEqualTo("text");
        });
    }

    @Test
    void itemDtoDeserializationTest() throws IOException {
        JsonContent<ItemDto> json = jacksonTester.write(itemDto);
        ItemDto deserializedItemDto = jacksonTester.parseObject(json.getJson());

        assertSoftly(softAssertions ->
                softAssertions.assertThat(deserializedItemDto)
                        .usingRecursiveComparison()
                        .isEqualTo(itemDto));
    }
}
