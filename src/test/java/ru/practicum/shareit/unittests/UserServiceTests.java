package ru.practicum.shareit.unittests;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.MapToUser;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

  @Mock
  private UserRepository userRepository;

  @Test
  void createUserTest() {
    var userService = new UserService(userRepository);

    var inputUserDto = new UserDto(0, "testUserName", "testUser@email.com");
    var expectedUser = new User(1L, inputUserDto.getName(), inputUserDto.getEmail());


    Mockito.when(userRepository.save(any())).thenReturn(expectedUser);

    var user = userService.add(inputUserDto);
    assertSoftly(softAssertions ->
        softAssertions.assertThat(user)
            .usingRecursiveComparison()
            .isEqualTo(MapToUser.toDto(expectedUser)));
  }

  @Test
  void updateUserTest() {
    var userService = new UserService(userRepository);

    var inputUserDto = new UserDto(1L, "testUserName", "testUser@email.com");
    var previousVersionUser = new User(1L, "oldName", "oldEmail");
    var expectedUser =  new User(1L, inputUserDto.getName(), inputUserDto.getEmail());


    Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(previousVersionUser));

    var user = userService.update(inputUserDto.getId(), inputUserDto);
    assertSoftly(softAssertions ->
        softAssertions.assertThat(user)
            .usingRecursiveComparison()
            .isEqualTo(MapToUser.toDto(expectedUser)));
  }

  @Test
  void getUserTest() {
    var userService = new UserService(userRepository);

    var expectedUser = new User(1L, "userName", "userEmail@mail.com");


    Mockito.when(userRepository.findById(expectedUser.getId())).thenReturn(Optional.of(expectedUser));

    var user = userService.get(expectedUser.getId());
    assertSoftly(softAssertions ->
        softAssertions.assertThat(user)
            .usingRecursiveComparison()
            .isEqualTo(MapToUser.toDto(expectedUser)));
  }

  @Test
  void getUsersTest() {
    var userService = new UserService(userRepository);

    var expectedUsers = List.of(new User(1L, "userName", "userEmail@mail.com"));

    Mockito.when(userRepository.findAll()).thenReturn(expectedUsers);

    var user = userService.getAll();
    assertSoftly(softAssertions ->
        softAssertions.assertThat(user)
            .usingRecursiveComparison()
            .isEqualTo(expectedUsers));
  }

  @Test
  void deleteUserTest() {
    var userService = new UserService(userRepository);
    var expectedUser = new User(1L, "userName", "userEmail@mail.com");


    var userId = 1L;
    Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(expectedUser));

    userService.delete(userId);

    Mockito.verify(userRepository, Mockito.times(1))
        .deleteById(userId);
  }
}
