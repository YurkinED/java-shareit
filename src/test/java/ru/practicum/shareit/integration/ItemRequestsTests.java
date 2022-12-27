package ru.practicum.shareit.integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestsTests {

  private final EntityManager em;
  private final ItemRequestService itemRequestService;

  @Test
  void createItemRequestTest() {
    var user = new User(0, "authorName", "mail@mail.com");
    var notOwnerUser = new User(0, "notOwnerName", "mail1@mail.com");
    var sourceItemRequest = ItemRequestDto.builder()
        .description("itemRequestDescription")
        .created(LocalDateTime.now())
        .build();

    em.persist(user);
    em.persist(notOwnerUser);
    var entity = ItemRequestMapper.toItemRequest(user.getId(), sourceItemRequest);
    em.persist(entity);
    em.flush();

    var targetItemRequests = itemRequestService.create(notOwnerUser.getId(), sourceItemRequest);
    assertSoftly(softAssertions ->
        softAssertions.assertThat(targetItemRequests)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(sourceItemRequest));
  }

  @Test
  void getItemRequestsTest() {
    var user = new User(0, "authorName", "mail@mail.com");
    var notOwnerUser = new User(0, "notOwnerName", "mail1@mail.com");
    var sourceItemRequests = List.of(
        ItemRequestDto.builder()
            .description("itemRequestDescription")
            .created(LocalDateTime.now())
            .build(),
        ItemRequestDto.builder()
            .description("secondItemRequestDescription")
            .created(LocalDateTime.now())
            .build()
    );

    em.persist(user);
    em.persist(notOwnerUser);
    for (var item : sourceItemRequests) {
      var entity = ItemRequestMapper.toItemRequest(user.getId(), item);
      em.persist(entity);
    }
    em.flush();

    var targetItemRequests = itemRequestService.getList(notOwnerUser.getId(), 0, 1000);
    assertSoftly(softAssertions ->
        softAssertions.assertThat(targetItemRequests.size())
            .isEqualTo(sourceItemRequests.size()));
  }

  @Test
  void getItemRequestsPageTest() {
    var user = new User(0, "authorName", "mail@mail.com");
    var notOwnerUser = new User(0, "notOwnerName", "mail1@mail.com");
    var sourceItemRequests = List.of(
        ItemRequestDto.builder()
            .description("itemRequestDescription")
            .created(LocalDateTime.now())
            .build(),
        ItemRequestDto.builder()
            .description("secondItemRequestDescription")
            .created(LocalDateTime.now())
            .build()
    );

    em.persist(user);
    em.persist(notOwnerUser);
    for (var item : sourceItemRequests) {
      var entity = ItemRequestMapper.toItemRequest(user.getId(), item);
      em.persist(entity);
    }
    em.flush();

    var targetItemRequests = itemRequestService.getList(notOwnerUser.getId(), 0, 2);
    assertSoftly(softAssertions ->
        softAssertions.assertThat(targetItemRequests.size())
            .isEqualTo(sourceItemRequests.size()));
  }

  @Test
  void getUserItemRequestsTest() {
    var user = new User(0, "authorName", "mail@mail.com");
    var notOwnerUser = new User(0, "notOwnerName", "mail1@mail.com");
    var sourceItemRequests = List.of(
        ItemRequestDto.builder()
            .description("itemRequestDescription")
            .created(LocalDateTime.now())
            .build(),
        ItemRequestDto.builder()
            .description("secondItemRequestDescription")
            .created(LocalDateTime.now())
            .build()
    );

    em.persist(user);
    em.persist(notOwnerUser);
    for (var item : sourceItemRequests) {
      var entity = ItemRequestMapper.toItemRequest(user.getId(), item);
      em.persist(entity);
    }
    em.flush();

    var targetItemRequests = itemRequestService.getByUser(user.getId());
    assertSoftly(softAssertions ->
        softAssertions.assertThat(targetItemRequests.size())
            .isEqualTo(sourceItemRequests.size()));
  }

  @Test
  void getItemRequestTest() {
    var user = new User(0, "authorName", "mail@mail.com");
    var notOwnerUser = new User(0, "notOwnerName", "mail1@mail.com");
    var sourceItemRequest = ItemRequest.builder()
        .description("itemRequestDescription")
        .createDateTime(LocalDateTime.now())
        .build();

    em.persist(user);
    em.persist(notOwnerUser);
    em.persist(sourceItemRequest);
    em.flush();

    var targetItemRequests = itemRequestService.get(user.getId(), sourceItemRequest.getId());
    assertSoftly(softAssertions ->
        softAssertions.assertThat(targetItemRequests)
            .usingRecursiveComparison()
            .ignoringFields("id", "items")
            .isEqualTo(ItemRequestMapper.toItemRequestDto(sourceItemRequest)));
  }

  @Test
  void getExceptionTest() {
    var user = new User(0, "authorName", "mail@mail.com");
    var notOwnerUser = new User(0, "notOwnerName", "mail1@mail.com");
    var sourceItemRequest = ItemRequest.builder()
            .description("itemRequestDescription")
            .createDateTime(LocalDateTime.now())
            .build();

    em.persist(user);
    em.persist(notOwnerUser);
    em.persist(sourceItemRequest);
    em.flush();
    assertThatThrownBy(() -> {
      itemRequestService.get(1000L, sourceItemRequest.getId());
    }).isInstanceOf(NotFoundException.class)
            .hasMessageContaining("Такого пользователь не существует");

    assertThatThrownBy(() -> {
      itemRequestService.get(user.getId(), 1000L);
    }).isInstanceOf(NoSuchElementException.class)
            .hasMessageContaining("Запроса не найдено");
  }
}