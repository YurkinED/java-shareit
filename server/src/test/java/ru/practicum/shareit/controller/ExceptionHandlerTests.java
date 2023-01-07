package ru.practicum.shareit.controller;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exceptions.BookingException;
import ru.practicum.shareit.exceptions.EmailException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

@WebMvcTest(controllers = UserController.class)
class ExceptionHandlerTests {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private UserDto userDto;
    private UserDto userDto1;
    private UserDto userDto2;
    private UserDto userDto3;

    private BookingDto bookingDto;

    @MockBean
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1L, "testUserName", "testUser");
        userDto1 = new UserDto(2L, "testUserName1", "testUser@mail.ru");
        userDto2 = new UserDto(3L, "testUserName2", "testUser@mail.ru");
        userDto3 = new UserDto(4L, "", "testUser@mail.ru");
        bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().minusDays(1));
    }

    @Test
    void createUserWithDoubleEmailExceptionTest() throws Exception {
        when(userService.add(userDto1))
                .thenReturn(userDto1);
        when(userService.add(userDto2))
                .thenThrow(new NotFoundException(null));

        var response = mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        response = mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse();
    }

    /*
        @Test
        void createUserWithEmptyNameExceptionTest() throws Exception {
            var response = mvc.perform(post("/users")
                            .content(mapper.writeValueAsString(userDto3))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().is4xxClientError())
                    .andReturn().getResponse();
        }
    */
    @Test
    void getBookingByUnknownUser() throws Exception {
        var bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(1L);
        bookingResponseDto.setStart(bookingDto.getStart());
        bookingResponseDto.setEnd(bookingDto.getEnd());
        var expectedBookings = List.of(bookingResponseDto);
        var response = mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 100)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse();
    }

    @Test
    void findNotExistUserExceptionTest() throws Exception {
        when(userService.get(anyLong()))
                .thenThrow(new NotFoundException("Такого пользователь не существует"));

        var response = mvc.perform(get("/users/{userId}", 1000L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse();
    }

    @Test
    void sendWrongEmailExceptionTest() throws Exception {
        when(userService.add(any()))
                .thenThrow(new EmailException("Некорректный емайл"));

        var response = mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse();
    }

    @Test
    void findNotExistBookingExceptionTest() throws Exception {
        when(userService.get(anyLong()))
                .thenThrow(new BookingException("Такого бронирования не существует"));

        var response = mvc.perform(get("/booking/{bookingId}", 1000L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse();
    }

    @Test
    void unsupportedStatusTest() throws Exception {
        var response = mvc.perform(get("/bookings")
                        .param("state", "UNSUPPORTED_STATUS")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse();
    }

    @Test
    void bookItemWrongDateTest() throws Exception {

        var response = mvc.perform(post("/booking")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse();
    }


}
