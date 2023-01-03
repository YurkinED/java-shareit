package ru.practicum.shareit.jpa;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DataJpaTest

public class CommentJpaTests {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void findAllByItem_IdTest() {
        var user = new User(0, "testUserName", "testUser@email.com");
        var item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setUser(user);

        Comment comment = new Comment();
        comment.setText("test comment");
        comment.setCreated(LocalDateTime.now());


        em.persist(user);
        em.persist(item);
        comment.setItem(item);
        em.persist(comment);

        var commentReturn = commentRepository.findAllByItem_Id(item.getId());

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(commentReturn.size())
                    .usingRecursiveComparison()
                    .isEqualTo(1);

            softAssertions.assertThat(commentReturn.get(0))
                    .usingRecursiveComparison()
                    .isEqualTo(comment);
        });
    }


    @Test
    void findByItemInTest() {
        var user = new User(0, "testUserName", "testUser@email.com");
        var item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setUser(user);

        Comment comment = new Comment();
        comment.setText("test comment");
        comment.setCreated(LocalDateTime.now());


        em.persist(user);
        em.persist(item);
        comment.setItem(item);
        em.persist(comment);

        var commentReturn = commentRepository.findByItemIn(List.of(item), Sort.unsorted());

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(commentReturn.size())
                    .usingRecursiveComparison()
                    .isEqualTo(1);

            softAssertions.assertThat(commentReturn.get(0))
                    .usingRecursiveComparison()
                    .isEqualTo(comment);
        });
    }
}
