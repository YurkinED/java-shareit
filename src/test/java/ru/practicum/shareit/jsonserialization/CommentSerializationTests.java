package ru.practicum.shareit.jsonserialization;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.CommentDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CommentSerializationTests {
    @Autowired
    private JacksonTester<Comment> jacksonTester;
    @Autowired
    private JacksonTester<CommentDto> jacksonTester2;
    private Comment comment;
    private CommentDto commentDto;

    LocalDateTime localDate;

    @BeforeEach
    void setUp() {
        localDate = LocalDateTime.now();
        comment = new Comment();
        comment.setId(1L);
        comment.setCreated(localDate);
        comment.setText("text");
        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setCreated(localDate);
        commentDto.setText("text");



    }

    @Test
    void itemDtoSerializationTest() throws IOException {
        JsonContent<Comment> json = jacksonTester.write(comment);
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
            assertThat(json).extractingJsonPathStringValue("$.text").isEqualTo("text");
        });
    }



    @Test
    void itemDtoSerializationTest2() throws IOException {
        JsonContent<CommentDto> json = jacksonTester2.write(commentDto);
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
            assertThat(json).extractingJsonPathStringValue("$.text").isEqualTo("text");
        });
    }

}