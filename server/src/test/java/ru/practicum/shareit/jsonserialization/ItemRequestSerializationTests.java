package ru.practicum.shareit.jsonserialization;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.io.IOException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@JsonTest
class ItemRequestSerializationTests {

  @Autowired
  private JacksonTester<ItemRequestDto> jacksonTester;
  private ItemRequestDto itemRequestDto;

  @BeforeEach
  void setUp() {
    itemRequestDto = ItemRequestDto.builder()
        .id(1L)
        .description("New item request")
        .build();
  }

  @Test
  void itemDtoSerializationTest() throws IOException {
    JsonContent<ItemRequestDto> json = jacksonTester.write(itemRequestDto);
    SoftAssertions.assertSoftly(softAssertions -> {
      assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
      assertThat(json).extractingJsonPathStringValue("$.description").isEqualTo("New item request");
    });
  }

  @Test
  void itemDtoDeserializationTest() throws IOException {
    JsonContent<ItemRequestDto> json = jacksonTester.write(itemRequestDto);
    ItemRequestDto deserializedItemDto = jacksonTester.parseObject(json.getJson());

    assertSoftly(softAssertions ->
        softAssertions.assertThat(deserializedItemDto)
            .usingRecursiveComparison()
            .isEqualTo(itemRequestDto));
  }
}
