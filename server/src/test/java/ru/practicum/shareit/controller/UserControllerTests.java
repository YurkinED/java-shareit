package ru.practicum.shareit.controller;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

@WebMvcTest(controllers = UserController.class)
class UserControllerTests {

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private UserService userService;

  @Autowired
  private MockMvc mvc;

  private UserDto userDto;

  @BeforeEach
  void setUp() {
    userDto = UserDto.builder().id(1L).name("testUserName").email("testUser@email.com").build();
  }

  @Test
  void createUserTest() throws Exception {

    when(userService.add(any())).thenReturn(userDto);

    var response = mvc.perform(post("/users")
        .content(mapper.writeValueAsString(userDto))
        .characterEncoding(StandardCharsets.UTF_8)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse();
    var responseObject = mapper.readValue(response.getContentAsString(), UserDto.class);
    assertSoftly(softAssertions ->
        softAssertions.assertThat(responseObject)
            .usingRecursiveComparison()
            .isEqualTo(userDto));
  }

  @Test
  void updateUserTest() throws Exception {
    when(userService.update(anyLong(), any())).thenReturn(userDto);

    var response = mvc.perform(patch("/users/{userId}", userDto.getId())
            .content(mapper.writeValueAsString(userDto))
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse();
    var responseObject = mapper.readValue(response.getContentAsString(), UserDto.class);
    assertSoftly(softAssertions ->
        softAssertions.assertThat(responseObject)
            .usingRecursiveComparison()
            .isEqualTo(userDto));
  }

  @Test
  void getUserTest() throws Exception {
    when(userService.get(anyLong())).thenReturn(userDto);

    var response = mvc.perform(get("/users/{userId}", userDto.getId())
        .characterEncoding(StandardCharsets.UTF_8)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse();
    var responseObject = mapper.readValue(response.getContentAsString(), UserDto.class);
    assertSoftly(softAssertions ->
        softAssertions.assertThat(responseObject)
            .usingRecursiveComparison()
            .isEqualTo(userDto));
  }

  @Test
  void getUsersTest() throws Exception {
    var users = List.of(userDto);
    when(userService.getAll()).thenReturn(users);

    var response = mvc.perform(get("/users")
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse();
    List<UserDto> responseObject = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
    });
    assertSoftly(softAssertions ->
        softAssertions.assertThat(responseObject)
            .usingRecursiveComparison()
            .isEqualTo(users));
  }

  @Test
  void deleteTest() throws Exception {
    mvc.perform(delete("/users/{userId}", userDto.getId())
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    Mockito.verify(userService, Mockito.times(1))
            .delete(userDto.getId());
  }
}
