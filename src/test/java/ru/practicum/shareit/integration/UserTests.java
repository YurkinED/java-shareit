package ru.practicum.shareit.integration;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserTests {

  private final EntityManager em;
  private final UserService userService;

  @Test
  void createUserTest() {
    var userDto = new UserDto(1L, "authorName", "mail@mail.com");
    var targetUsers = userService.add(userDto);

    assertSoftly(softAssertions ->
        softAssertions.assertThat(targetUsers)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(userDto));
  }


  @Test
  void updateUserTest() {
    var user = new User(0, "authorName", "mail@mail.com");
    em.persist(user);
    var userDto = new UserDto(user.getId(), "authorNewName", "newmail@mail.com");
    var targetUsers = userService.update(user.getId(), userDto);

    assertSoftly(softAssertions ->
        softAssertions.assertThat(targetUsers)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(userDto));
  }

  @Test
  void getUserTest() {
    var user = new User(0, "authorName", "mail@mail.com");
    em.persist(user);
    em.flush();

    var targetUsers = userService.get(user.getId());
    assertSoftly(softAssertions ->
        softAssertions.assertThat(targetUsers)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(user));
  }

  @Test
  void getUsersTest() {
    var user = new User(0, "authorName", "mail@mail.com");
    em.persist(user);
    em.flush();

    var targetUsers = userService.getAll();
    assertSoftly(softAssertions ->
        softAssertions.assertThat(targetUsers.size())
            .isEqualTo(1));
  }

  @Test
  void deleteUsersTest() {
    var user = new User(0, "authorName", "mail@mail.com");
    em.persist(user);
    em.flush();
    var targetUsers = userService.delete(user.getId());
    assertSoftly(softAssertions ->
            softAssertions.assertThat(targetUsers)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(user));
  }
}
