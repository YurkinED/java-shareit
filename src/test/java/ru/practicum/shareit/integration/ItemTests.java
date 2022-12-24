package ru.practicum.shareit.integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.validation.ValidationException;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.BookingException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.MapToItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemTests {

    private final EntityManager em;
    private final ItemService itemService;
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void getItemsTest() {
        var user = new User(0, "authorName", "mail@mail.com");
        var sourceItems = List.of(
                ItemDto.builder()
                        .name("itemName")
                        .description("itemDescription")
                        .available(true)
                        .build(),
                ItemDto.builder()
                        .name("secondItemName")
                        .description("secondItemDescription")
                        .available(true)
                        .build()
        );

        em.persist(user);
        for (var item : sourceItems) {
            var entity = MapToItem.fromDto(item, user);
            em.persist(entity);
        }
        em.flush();

        var targetItems = itemService.getAll(user.getId());
        assertSoftly(softAssertions ->
                softAssertions.assertThat(targetItems.size())
                        .isEqualTo(sourceItems.size()));
    }

    @Test
    void getItemsWithBookingInfoTest() {
        var user = new User(0, "authorName", "mail@mail.com");
        em.persist(user);
        var sourceItem = new Item();
        sourceItem.setName("itemName");
        sourceItem.setDescription("itemDescription");
        sourceItem.setAvailable(true);
        sourceItem.setUser(user);
        em.persist(sourceItem);

        var lastBooking = new Booking();
        lastBooking.setBooker(user);
        lastBooking.setItem(sourceItem);
        lastBooking.setStart(LocalDateTime.now().minusDays(10));
        lastBooking.setEnd(LocalDateTime.now().minusDays(5));

        var featureBooking = new Booking();
        featureBooking.setBooker(user);
        featureBooking.setItem(sourceItem);
        featureBooking.setStart(LocalDateTime.now().plusDays(10));
        featureBooking.setEnd(LocalDateTime.now().plusDays(15));
        em.persist(lastBooking);
        em.persist(featureBooking);
        em.flush();

        var targetItems = itemService.getAll(user.getId());
        assertSoftly(softAssertions ->
                softAssertions.assertThat(targetItems.get(0))
                        .usingRecursiveComparison()
                        .ignoringFields("booker", "requestId", "lastBooking", "nextBooking", "comments")
                        .isEqualTo(sourceItem));
    }

    @Test
    void getItemsPageTest() {
        var user = new User(0, "authorName", "mail@mail.com");

        var sourceItems = List.of(
                ItemDto.builder()
                        .name("itemName")
                        .description("itemDescription")
                        .available(true)
                        .build(),
                ItemDto.builder()
                        .name("secondItemName")
                        .description("secondItemDescription")
                        .available(true)
                        .build()
        );

        em.persist(user);
        for (var item : sourceItems) {
            var entity = MapToItem.fromDto(item, user);
            em.persist(entity);
        }
        em.flush();

        var targetItems = itemService.getAll(user.getId());
        assertSoftly(softAssertions ->
                softAssertions.assertThat(targetItems.size())
                        .isEqualTo(sourceItems.size()));
    }

    @Test
    void getItemTest() {
        var user = new User(0, "authorName", "mail@mail.com");
        em.persist(user);

        var sourceItem = new Item();
        sourceItem.setName("itemName");
        sourceItem.setDescription("itemDescription");
        sourceItem.setAvailable(true);
        sourceItem.setUser(user);
        em.persist(sourceItem);
        em.flush();

        var targetItem = itemService.get(user.getId(), sourceItem.getId());
        assertSoftly(softAssertions ->
                softAssertions.assertThat(targetItem)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "lastBooking", "nextBooking", "comments")
                        .isEqualTo(sourceItem));
    }

    @Test
    void searchItemsTest() {
        var user = new User(0, "authorName", "mail@mail.com");
        var sourceItems = List.of(
                ItemDto.builder()
                        .name("itemName")
                        .description("itemDescription")
                        .available(true)
                        .build(),
                ItemDto.builder()
                        .name("secondItemName")
                        .description("secondItemDescription")
                        .available(true)
                        .build()
        );

        em.persist(user);
        for (var item : sourceItems) {
            var entity = MapToItem.fromDto(item, user);
            em.persist(entity);
        }
        em.flush();

        var targetItems = itemService.search("itemName");
        assertSoftly(softAssertions ->
                softAssertions.assertThat(targetItems.size())
                        .isEqualTo(sourceItems.size()));
    }

    @Test
    void searchItemsPageTest() {
        var user = new User(0, "authorName", "mail@mail.com");
        var sourceItems = List.of(
                ItemDto.builder()
                        .name("itemName")
                        .description("itemDescription")
                        .available(true)
                        .build(),
                ItemDto.builder()
                        .name("secondItemName")
                        .description("secondItemDescription")
                        .available(true)
                        .build()
        );

        em.persist(user);
        for (var item : sourceItems) {
            var entity = MapToItem.fromDto(item, user);
            em.persist(entity);
        }
        em.flush();

        var targetItems = itemService.search("itemName");
        assertSoftly(softAssertions ->
                softAssertions.assertThat(targetItems.size())
                        .isEqualTo(sourceItems.size()));
    }

    @Test
    void findItemByRequestIdTest() {
        var user = new User(0, "authorName", "mail@mail.com");
        em.persist(user);

        var request = ItemRequest.builder()
                .description("requestDescription")
                .createDateTime(LocalDateTime.now())
                .requesterId(user.getId())
                .build();
        em.persist(request);

        var sourceItem = new Item();
        sourceItem.setName("itemName");
        sourceItem.setDescription("itemDescription");
        sourceItem.setAvailable(true);
        sourceItem.setUser(user);
        sourceItem.setRequestId(request.getId());
        em.persist(sourceItem);
        em.flush();

        var targetItems = itemService.findItemByRequestId(sourceItem.getRequestId());
        assertSoftly(softAssertions ->
                softAssertions.assertThat(targetItems.get(0))
                        .usingRecursiveComparison()
                        .ignoringFields("id")
                        .isEqualTo(sourceItem));
    }

    @Test
    void createItemTest() {
        var user = new User(0, "authorName", "mail@mail.com");
        var itemDto = ItemDto.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .build();

        em.persist(user);

        itemService.add(user.getId(), itemDto);

        TypedQuery<Item> query = em.createQuery("select i from Item i where i.name = :name", Item.class);
        var item = query.setParameter("name", itemDto.getName()).getSingleResult();

        assertSoftly(softAssertions ->
                softAssertions.assertThat(MapToItem.toDto(item))
                        .usingRecursiveComparison()
                        .ignoringFields("id")
                        .isEqualTo(itemDto));
    }

    @Test
    void updateItemTest() {
        var user = new User(0, "authorName", "mail@mail.com");
        em.persist(user);

        var item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setUser(user);
        em.persist(item);

        var itemDto = ItemDto.builder()
                .id(item.getId())
                .name("itemNewName")
                .description("itemNewDescription")
                .available(false)
                .build();
        itemService.update(user.getId(), item.getId(), itemDto);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        var updateItem = query.setParameter("name", itemDto.getName()).getSingleResult();

        assertSoftly(softAssertions ->
                softAssertions.assertThat(MapToItem.toDto(updateItem))
                        .usingRecursiveComparison()
                        .ignoringFields("id")
                        .isEqualTo(itemDto));
    }

    @Test
    void addCommentTest() {
        var user = new User(0, "authorName", "mail@mail.com");
        em.persist(user);

        var item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setUser(user);
        em.persist(item);

        var item2 = new Item();
        item2.setName("itemName");
        item2.setDescription("itemDescription");
        item2.setAvailable(true);
        item2.setUser(user);
        em.persist(item2);


        var itemDto = ItemDto.builder()
                .id(item.getId())
                .name("itemNewName")
                .description("itemNewDescription")
                .available(false)
                .build();

        CommentDto commentDto = new CommentDto();
        commentDto.setText("test comment");
        commentDto.setAuthorName("Author");
        commentDto.setCreated(LocalDateTime.now());

        CommentDto commentDto2 = new CommentDto();
        commentDto2.setText("test comment");
        commentDto2.setAuthorName("Author");
        commentDto2.setCreated(LocalDateTime.now());


        var booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(10));
        booking.setEnd(LocalDateTime.now().minusDays(5));
        em.persist(booking);
        em.flush();

        itemService.addComment(user.getId(), item.getId(), commentDto);

        TypedQuery<Comment> query = em.createQuery("select c from Comment c where c.item.id = :item_id", Comment.class);
        var comment = query.setParameter("item_id", item.getId()).getSingleResult();

        assertSoftly(softAssertions ->
                softAssertions.assertThat(comment)
                        .usingRecursiveComparison()
                        .ignoringFields("id")
                        .isEqualTo(CommentMapper.commentDtoToComment(item, user, commentDto)));
    }


    @Test
    void exceptionTest() {
        var user = new User(0, "authorName", "mail@mail.com");
        var itemDto = ItemDto.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .build();

        em.persist(user);

        assertThatThrownBy(() -> {
            itemService.add(2L, itemDto);
        }).isInstanceOf(ValidationException.class)
                .hasMessageContaining("Такого пользователь не существует");

    }

    @Test
    void exception2Test() {
        var user = new User(0, "authorName", "mail@mail.com");
        var user2 = new User(0, "authorName2", "mail2@mail.com");
        em.persist(user);
        em.persist(user2);

        var item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setUser(user);
        em.persist(item);

        var itemDto = ItemDto.builder()
                .id(item.getId())
                .name("itemNewName")
                .description("itemNewDescription")
                .available(false)
                .build();

        CommentDto commentDto = new CommentDto();
        commentDto.setText("test comment");
        commentDto.setAuthorName("Author");
        commentDto.setCreated(LocalDateTime.now());

        CommentDto commentDto2 = new CommentDto();
        commentDto2.setText("test comment");
        commentDto2.setAuthorName("Author");
        commentDto2.setCreated(LocalDateTime.now());


        var booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(10));
        booking.setEnd(LocalDateTime.now().minusDays(5));
        em.persist(booking);
        em.flush();


        assertThatThrownBy(() -> {
            itemService.addComment(user2.getId(), item.getId(), commentDto);
        }).isInstanceOf(BookingException.class)
                .hasMessageContaining("Вы не можете оставить отзыв на эту вещь");

    }

}
