package ru.practicum.shareit.jpa;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.apache.coyote.Request;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@DataJpaTest
class ItemJpaTests {

  @Autowired
  private TestEntityManager em;

  @Autowired
  private ItemRepository itemRepository;

  @Test
  void findAllByNameOrDescriptionTest() {
    var user = new User(0, "testUserName", "testUser@email.com");
    var item = new Item();
    item.setName("itemName");
    item.setDescription("itemDescription");
    item.setAvailable(true);
    item.setUser(user);

    em.persist(user);
    em.persist(item);

    var items = itemRepository.search("itemName");

    assertSoftly(softAssertions -> {
      softAssertions.assertThat(items.size())
          .usingRecursiveComparison()
          .isEqualTo(1);

      softAssertions.assertThat(items.get(0))
          .usingRecursiveComparison()
          .isEqualTo(item);
    });
  }

  @Test
  void findAllByUserIdTest() {
    var user = new User(0, "testUserName", "testUser@email.com");
    var item = new Item();
    item.setName("itemName");
    item.setDescription("itemDescription");
    item.setAvailable(true);
    item.setUser(user);

    em.persist(user);
    em.persist(item);

    var items = itemRepository.findAllByUserId(user.getId());

    assertSoftly(softAssertions -> {
      softAssertions.assertThat(items.size())
              .usingRecursiveComparison()
              .isEqualTo(1);

      softAssertions.assertThat(items.get(0))
              .usingRecursiveComparison()
              .isEqualTo(item);
    });
  }
/*
  @Test
  void findItemByRequestIdTest() {
    var user = new User(0, "testUserName", "testUser@email.com");
    var item = new Item();
    item.setName("itemName");
    item.setDescription("itemDescription");
    item.setAvailable(true);
    item.setUser(user);


    var request=new ItemRequest();
    request.setCreateDateTime(LocalDateTime.now());
    request.setDescription("test");


    em.persist(user);
    em.persist(request);
    item.setRequestId(request.getId());;
    em.persist(item);

    var items = itemRepository.findItemByRequestId(1L);

    assertSoftly(softAssertions -> {
      softAssertions.assertThat(items.size())
              .usingRecursiveComparison()
              .isEqualTo(1);

      softAssertions.assertThat(items.get(0))
              .usingRecursiveComparison()
              .isEqualTo(item);
    });
  }*/
}
