package ru.practicum.shareit.controller;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.user.model.User;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTests {

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private BookingService bookingService;

  @Autowired
  private MockMvc mvc;

  private BookingDto bookingDto;

  private BookingResponseDto bookingResponseDto;

  private User user;

  @BeforeEach
  void setUp() {
    bookingDto = new BookingDto();
    bookingDto.setStart(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS));
    bookingDto.setEnd(LocalDateTime.now().plusHours(5).truncatedTo(ChronoUnit.SECONDS));
    bookingResponseDto = new BookingResponseDto();
    bookingResponseDto.setId(1L);
    bookingResponseDto.setStart(bookingDto.getStart());
    bookingResponseDto.setEnd(bookingDto.getEnd());
    user = new User(1L, "testUserName", "testUser@email.com");
  }

  @Test
  void bookItemTest() throws Exception {
    when(bookingService.add(anyLong(), any()))
            .thenReturn(bookingResponseDto);

    var response = mvc.perform(post("/bookings")
            .content(mapper.writeValueAsString(bookingDto))
            .header("X-Sharer-User-Id", user.getId())
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse();
    var responseObject = mapper.readValue(response.getContentAsString(), BookingResponseDto.class);
    assertSoftly(softAssertions ->
        softAssertions.assertThat(responseObject)
            .usingRecursiveComparison()
            .isEqualTo(bookingResponseDto));
  }

  @Test
  void updateStatusTest() throws Exception {
    when(bookingService.updateStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingResponseDto);

    var response = mvc.perform(patch("/bookings/{bookingId}", bookingResponseDto.getId())
                    .param("approved", "true")
                    .header("X-Sharer-User-Id", user.getId())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse();
    var responseObject = mapper.readValue(response.getContentAsString(), BookingResponseDto.class);
    assertSoftly(softAssertions ->
            softAssertions.assertThat(responseObject)
                    .usingRecursiveComparison()
                    .isEqualTo(bookingResponseDto));
  }

  @Test
  void getBookingInfoTest() throws Exception {
    when(bookingService.getByAuthorOrOwner(anyLong(), anyLong())).thenReturn(bookingResponseDto);

    var response = mvc.perform(get("/bookings/{bookingId}", bookingResponseDto.getId())
                    .header("X-Sharer-User-Id", user.getId())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse();
    var responseObject = mapper.readValue(response.getContentAsString(), BookingResponseDto.class);
    assertSoftly(softAssertions ->
            softAssertions.assertThat(responseObject)
                    .usingRecursiveComparison()
                    .isEqualTo(bookingResponseDto));
  }

  @Test
  void getBookingByUser() throws Exception {
    var expectedBookings = List.of(bookingResponseDto);
    when(bookingService.getSort(anyLong(), any(), any(), any())).thenReturn(expectedBookings);
    var response = mvc.perform(get("/bookings")
                    .param("state", "ALL")
                    .header("X-Sharer-User-Id", user.getId())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse();
    List<BookingResponseDto> responseObject = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
    });
    assertSoftly(softAssertions ->
            softAssertions.assertThat(responseObject)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedBookings));
  }




  @Test
  void getAllOwnerBookingInfoTest() throws Exception {
    var expectedBookings = List.of(bookingResponseDto);
    when(bookingService.getByItemOwner(anyLong(),any(), any(), any()))
            .thenReturn(expectedBookings);

    var response = mvc.perform(get("/bookings/owner")
                    .header("X-Sharer-User-Id", user.getId())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse();
    List<BookingResponseDto> responseObject = mapper.readValue(response.getContentAsString(),
            new TypeReference<>() {
            });
    assertSoftly(softAssertions ->
            softAssertions.assertThat(responseObject)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedBookings));
  }



}
