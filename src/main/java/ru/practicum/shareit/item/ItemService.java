package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.MapToItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWithDateBooking;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ItemService {
    //private final ItemStorage itemStorage;
    private final ItemRepository itemStorage;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    public List<ItemWithDateBooking> getAll(long user) {
        if (!userRepository.existsById(user)) {
            throw new NotFoundException("Такого пользователя не существует");
        }
        List<Item> items = itemStorage.findAllByUser(user);
        List<ItemWithDateBooking> itemsWithDateBookingDto = new ArrayList<>();
        for (Item item : items) {
            itemsWithDateBookingDto.add(MapToItem.itemToItemWithDateBookingDto(item,
                    bookingRepository.findAllByItem_IdAndItem_User(item.getId(), item.getUser()),
                    commentRepository.findAllByItem_Id(item.getId())));
        }
        return itemsWithDateBookingDto;
    }

    @Transactional
    public ItemDto add(long user, ItemDto itemDto) {
        if (!userRepository.existsById(user)) {
            throw new NoItemUserException("Такого пользователь не существует");
        }
        Item item = itemStorage.save(MapToItem.fromDto(itemDto, user));
        return MapToItem.toDto(item);
    }

    @Transactional
    public ItemDto update(long user, long itemId, ItemDto itemDto) {
        if (!itemStorage.existsById(itemId)) {
            throw new ValidationException("Такой вещи не существует");
        }
        if (!userRepository.existsById(user)) {
            throw new NoUserException("Такого пользователь не существует");
        }
        if (itemStorage.getById(itemId).getUser() != user) {
            throw new NoItemUserException("Этот предмет принадлежит другому пользователю");
        }


        itemDto.setId(itemId);
        Item updateItem = itemStorage.getById(itemId);
        if (itemDto.getName() != null && !updateItem.getName().equals(itemDto.getName())) {
            updateItem.setName(itemDto.getName());
        }
        if (!updateItem.getDescription().equals(itemDto.getDescription()) && itemDto.getDescription() != null) {
            updateItem.setDescription(itemDto.getDescription());
        }
        if (updateItem.getAvailable() != itemDto.getAvailable() && itemDto.getAvailable() != null) {
            updateItem.setAvailable(itemDto.getAvailable());
        }

        Item item = itemStorage.save(updateItem);
        return MapToItem.toDto(item);
    }

    public ItemWithDateBooking get(long userId, long itemId) {
        if (!itemStorage.existsById(itemId)) {
            throw new NoItemUserException("Такой вещи не существует");
        }
        return MapToItem.itemToItemWithDateBookingDto(itemStorage.getById(itemId),
                bookingRepository.findAllByItem_IdAndItem_User(itemId, userId),
                commentRepository.findAllByItem_Id(itemId));
    }

    public List<ItemDto> search(String searchText) {
        return itemStorage.search("%" + searchText + "%").stream()
                .map(x -> MapToItem.toDto(x)).collect(Collectors.toList());

    }

    @Transactional
    public CommentDto addComment(long userId, long itemId, CommentDto comment) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (!itemStorage.existsById(itemId)) {
            throw new NotFoundException("Вещь не найдена");
        }
        if (bookingRepository.findAllByItem_IdAndBooker_IdAndEndIsBefore(itemId, userId, LocalDateTime.now())
                .isEmpty()) {
            throw new BookingException("Вы не можете оставить отзыв на эту вещь");
        }
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.commentToCommentDto(commentRepository.save(CommentMapper
                .commentDtoToComment(itemStorage.getById(itemId),
                        userRepository.getById(userId), comment)));
    }


}
