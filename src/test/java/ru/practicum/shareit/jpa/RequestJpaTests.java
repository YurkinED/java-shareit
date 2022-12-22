package ru.practicum.shareit.jpa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.SoftAssertions.assertSoftly;


@DataJpaTest
public class RequestJpaTests {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private RequestRepository requestRepository;

    @Test
    void findAllByRequesterIdTest() {
        var user = new User(0, "testUserName", "testUser@email.com");
        var item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setUser(user);
        em.persist(user);
        var itemRequest = new ItemRequest();
        itemRequest.setRequesterId(user.getId());
        itemRequest.setDescription("test");
        itemRequest.setCreateDateTime(LocalDateTime.now().minusDays(10).truncatedTo(ChronoUnit.SECONDS));
        em.persist(itemRequest);
        item.setRequestId(itemRequest.getId());
        em.persist(item);


        var requests = requestRepository.findAllByRequesterId(user.getId(), Sort.by("createDateTime").descending());

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(requests.size())
                    .usingRecursiveComparison()
                    .isEqualTo(1);

            softAssertions.assertThat(requests.get(0))
                    .usingRecursiveComparison()
                    .isEqualTo(itemRequest);
        });
    }

    @Test
    void findAllByRequesterIdNotTest() {
        var user = new User(0, "testUserName", "testUser@email.com");
        var user2 = new User(0, "testUserName", "testUser2@email.com");
        var item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setUser(user);
        em.persist(user);
        em.persist(user2);
        var itemRequest = new ItemRequest();
        itemRequest.setRequesterId(user.getId());
        itemRequest.setDescription("test");
        itemRequest.setCreateDateTime(LocalDateTime.now().minusDays(10).truncatedTo(ChronoUnit.SECONDS));
        em.persist(itemRequest);
        item.setRequestId(itemRequest.getId());
        em.persist(item);


        var requests = requestRepository.findAllByRequesterIdNot(user2.getId(), Pageable.unpaged());

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(requests.size())
                    .usingRecursiveComparison()
                    .isEqualTo(1);

            softAssertions.assertThat(requests.get(0))
                    .usingRecursiveComparison()
                    .isEqualTo(itemRequest);
        });
    }
}

