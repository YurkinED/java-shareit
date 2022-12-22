package ru.practicum.shareit.controller;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTests {

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private ItemRequestService itemRequestService;

  @Autowired
  private MockMvc mvc;

  private User user;

  private ItemRequestDto itemRequestDto;

  @BeforeEach
  void setUp() {
    user = new User(1L, "testUserName", "testUser@email.com");
    itemRequestDto = ItemRequestDto.builder()
        .id(1L)
        .description("itemDescription")
        .created(LocalDateTime.now())
        .build();
  }

  @Test
  void createItemRequestTest() throws Exception {
    when(itemRequestService.createItemRequest(anyLong(), any())).thenReturn(itemRequestDto);

    var response = mvc.perform(post("/requests")
            .content(mapper.writeValueAsString(itemRequestDto))
            .header("X-Sharer-User-Id", user.getId())
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse();
    var responseObject = mapper.readValue(response.getContentAsString(), ItemRequestDto.class);
    assertSoftly(softAssertions ->
        softAssertions.assertThat(responseObject)
            .usingRecursiveComparison()
            .isEqualTo(itemRequestDto));
  }

  @Test
  void getUserItemRequestsTest() throws Exception {
    var itemRequests = List.of(itemRequestDto);
    when(itemRequestService.getUserItemRequests(anyLong())).thenReturn(itemRequests);

    var response = mvc.perform(get("/requests")
            .header("X-Sharer-User-Id", user.getId())
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse();
    List<ItemRequestDto> responseObject = mapper.readValue(response.getContentAsString(),
        new TypeReference<>() {
        });
    assertSoftly(softAssertions ->
        softAssertions.assertThat(responseObject)
            .usingRecursiveComparison()
            .isEqualTo(itemRequests));
  }

  @Test
  void getItemRequestsTest() throws Exception {
    var itemRequests = List.of(itemRequestDto);
    when(itemRequestService.getItemRequests(anyLong(), any(), any())).thenReturn(itemRequests);

    var response = mvc.perform(get("/requests/all")
            .header("X-Sharer-User-Id", user.getId())
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse();
    List<ItemRequestDto> responseObject = mapper.readValue(response.getContentAsString(),
        new TypeReference<>() {
        });
    assertSoftly(softAssertions ->
        softAssertions.assertThat(responseObject)
            .usingRecursiveComparison()
            .isEqualTo(itemRequests));
  }

  @Test
  void getItemRequestByIdTest() throws Exception {
    when(itemRequestService.getItemRequest(anyLong(), anyLong())).thenReturn(itemRequestDto);

    var response = mvc.perform(get("/requests/{requestId}", itemRequestDto.getId())
            .header("X-Sharer-User-Id", user.getId())
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse();
    var responseObject = mapper.readValue(response.getContentAsString(), ItemRequestDto.class);
    assertSoftly(softAssertions ->
        softAssertions.assertThat(responseObject)
            .usingRecursiveComparison()
            .isEqualTo(itemRequestDto));
  }
}
