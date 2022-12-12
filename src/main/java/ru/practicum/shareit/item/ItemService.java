package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.MapToItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWithDateBooking;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemStorage;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    public List<ItemWithDateBooking> getAll(long user) {
        if (!userRepository.existsById(user)) {
            throw new NotFoundException("Такого пользователя не существует");
        }

        List<Item> items = itemStorage.findAllByUserId(user);
        List<ItemWithDateBooking> itemsWithDateBookingDto = new ArrayList<>();

        Map<Item, List<Comment>> comments = commentRepository.findByItemIn(items, Sort.by(DESC, "created"))
                .stream()
                .collect(groupingBy(Comment::getItem, toList()));
        Map<Item, List<Booking>> bookings = bookingRepository.findByItemInAndStatusEquals(items, BookingStatus.APPROVED)
                .stream()
                .collect(groupingBy(Booking::getItem, toList()));
        for (Item item : items) {
            itemsWithDateBookingDto.add(MapToItem.itemToItemWithDateBookingDto(item,
                    bookings.getOrDefault(item, Collections.emptyList()),
                    comments.getOrDefault(item, Collections.emptyList())
            ));
        }
        return itemsWithDateBookingDto;
    }

    @Transactional
    public ItemDto add(long user, ItemDto itemDto) {
        Item item = itemStorage.save(MapToItem.fromDto(itemDto, userRepository.findById(user).orElseThrow(() -> {
                    throw new ValidationException("Такого пользователь не существует");
                })
        ));
        return MapToItem.toDto(item);
    }

    @Transactional
    public ItemDto update(long user, long itemId, ItemDto itemDto) {
        if (itemStorage.findById(itemId).orElseThrow(() -> {
            throw new NoUserException("Такого пользователь не существует");
        }).getUser().getId() != user) {
            throw new NoItemUserException("Этот предмет принадлежит другому пользователю");
        }

        itemDto.setId(itemId);
        Item updateItem = itemStorage.findById(itemId).orElseThrow(() -> {
            throw new NoItemUserException("Такого пользователь не существует");
        });
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            updateItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            updateItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updateItem.setAvailable(itemDto.getAvailable());
        }
        return MapToItem.toDto(updateItem);
    }

    public ItemWithDateBooking get(long userId, long itemId) {
        return MapToItem.itemToItemWithDateBookingDto(itemStorage.findById(itemId).orElseThrow(() -> {
                    throw new NoItemUserException("Такого вещи не существует");
                }),
                bookingRepository.findAllByItem_IdAndItem_User_Id(itemId, userId),
                commentRepository.findAllByItem_Id(itemId));
    }

    public List<ItemDto> search(String searchText) {
        return itemStorage.search("%" + searchText + "%").stream()
                .map(x -> MapToItem.toDto(x)).collect(toList());

    }

    @Transactional
    public CommentDto addComment(long userId, long itemId, CommentDto comment) {
        if (bookingRepository.findAllByItem_IdAndBooker_IdAndEndIsBefore(itemId, userId, LocalDateTime.now())
                .isEmpty()) {
            throw new BookingException("Вы не можете оставить отзыв на эту вещь");
        }
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.commentToCommentDto(commentRepository.save(CommentMapper
                .commentDtoToComment(itemStorage.findById(itemId).orElseThrow(() -> {
                            throw new NotFoundException("Вещь не найдена");
                        }),
                        userRepository.findById(userId).orElseThrow(() -> {
                            throw new NotFoundException("Пользователь не найден");
                        }), comment)));
    }


}
