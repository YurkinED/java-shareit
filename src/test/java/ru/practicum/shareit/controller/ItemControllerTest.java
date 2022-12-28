package ru.practicum.shareit.controller;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemWithDateBooking;
import ru.practicum.shareit.user.model.User;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private ItemService itemService;

  @Autowired
  private MockMvc mvc;

  private User user;

  private ItemDto itemDto;

  private ItemWithDateBooking itemWithDateBooking;

  @BeforeEach
  void setUp() {
    user = new User(1L, "testUserName", "testUser@email.com");
    itemDto = ItemDto.builder()
        .id(1L)
        .name("itemName")
        .description("itemDescription")
        .available(true)
        .build();
    itemWithDateBooking = new  ItemWithDateBooking();
    itemWithDateBooking.setId(itemDto.getId());
    itemWithDateBooking.setDescription(itemDto.getDescription());
    itemWithDateBooking.setAvailable(itemDto.getAvailable());
    itemWithDateBooking.setName(itemDto.getName());
  }

  @Test
  void createItemTest() throws Exception {
    when(itemService.add(anyLong(), any())).thenReturn(itemDto);

    var response = mvc.perform(post("/items")
            .content(mapper.writeValueAsString(itemDto))
            .header("X-Sharer-User-Id", user.getId())
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse();
    var responseObject = mapper.readValue(response.getContentAsString(), ItemDto.class);
    assertSoftly(softAssertions ->
        softAssertions.assertThat(responseObject)
            .usingRecursiveComparison()
            .isEqualTo(itemDto));
  }

  @Test
  void updateItemTest() throws Exception {
    when(itemService.update(anyLong(), anyLong(), any())).thenReturn(itemDto);

    var response = mvc.perform(patch("/items/{itemId}", itemDto.getId())
            .content(mapper.writeValueAsString(itemDto))
            .header("X-Sharer-User-Id", user.getId())
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse();
    var responseObject = mapper.readValue(response.getContentAsString(), ItemDto.class);
    assertSoftly(softAssertions ->
        softAssertions.assertThat(responseObject)
            .usingRecursiveComparison()
            .isEqualTo(itemDto));
  }

  @Test
  void getItemTest() throws Exception {
    when(itemService.get(anyLong(), anyLong())).thenReturn(itemWithDateBooking);

    var response = mvc.perform(get("/items/{itemId}", itemDto.getId())
            .header("X-Sharer-User-Id", user.getId())
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse();
    var responseObject = mapper.readValue(response.getContentAsString(), ItemWithDateBooking.class);
    assertSoftly(softAssertions ->
        softAssertions.assertThat(responseObject)
            .usingRecursiveComparison()
            .isEqualTo(itemWithDateBooking));
  }

  @Test
  void getItemsTest() throws Exception {
    var items = List.of(itemWithDateBooking);
    when(itemService.getAll(anyLong())).thenReturn(items);

    var response = mvc.perform(get("/items")
            .header("X-Sharer-User-Id", user.getId())
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse();
    List<ItemWithDateBooking> responseObject = mapper.readValue(response.getContentAsString(),
        new TypeReference<>() {
        });
    assertSoftly(softAssertions ->
        softAssertions.assertThat(responseObject)
            .usingRecursiveComparison()
            .isEqualTo(items));
  }

  @Test
  void searchItemsTest() throws Exception {
    var items = List.of(itemDto);
    when(itemService.search(anyString())).thenReturn(items);

    var response = mvc.perform(get("/items/search")
            .param("text", "searchedText")
            .header("X-Sharer-User-Id", user.getId())
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse();
    List<ItemDto> responseObject = mapper.readValue(response.getContentAsString(),
        new TypeReference<>() {
        });
    assertSoftly(softAssertions ->
        softAssertions.assertThat(responseObject)
            .usingRecursiveComparison()
            .isEqualTo(items));
  }

  @Test
  void addCommentTest() throws Exception {
    CommentDto comment = new CommentDto();
    comment.setId(1L);
    comment.setAuthorName("commentAuthorName");
    comment.setCreated(LocalDateTime.now());
    comment.setText("commentText");
    when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(comment);

    var response = mvc.perform(post("/items/{itemId}/comment", itemDto.getId())
            .content(mapper.writeValueAsString(comment))
            .header("X-Sharer-User-Id", user.getId())
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse();
    var responseObject = mapper.readValue(response.getContentAsString(), CommentDto.class);
    assertSoftly(softAssertions ->
        softAssertions.assertThat(responseObject)
            .usingRecursiveComparison()
            .isEqualTo(comment));
  }
}
