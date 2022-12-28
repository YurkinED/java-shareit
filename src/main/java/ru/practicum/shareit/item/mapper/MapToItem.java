package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWithDateBooking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MapToItem {
    public static Item fromDto(ItemDto itemDto, User user) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setUser(user);
        item.setRequestId(itemDto.getRequestId());

        return item;
    }

    public static ItemDto toDto(Item item) {
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .build();

        return itemDto;
    }

    public static ItemWithDateBooking itemToItemWithDateBookingDto(Item item, List<Booking> bookingList,
                                                                   List<Comment> comments) {
        ItemWithDateBooking itemWithDateBookingDto = new ItemWithDateBooking();
        Optional<Booking> last = bookingList.stream().filter(booking -> (booking.getEnd()
                .isBefore(LocalDateTime.now()) ||
                booking.getEnd().isEqual(LocalDateTime.now()) || (booking.getStart().isBefore(LocalDateTime.now()) || booking.getStart().isEqual(LocalDateTime.now())))
        ).findFirst();
        Optional<Booking> next = bookingList.stream().filter(booking -> booking.getStart()
                .isAfter(LocalDateTime.now())).findFirst();

        if (last.isEmpty()) {
            itemWithDateBookingDto.setLastBooking(null);
        } else {
            itemWithDateBookingDto.setLastBooking(new ItemWithDateBooking.Booking(last.get().getId(),
                    last.get().getBooker().getId()));
        }
        if (next.isEmpty()) {
            itemWithDateBookingDto.setNextBooking(null);
        } else {
            itemWithDateBookingDto.setNextBooking(new ItemWithDateBooking.Booking(next.get().getId(),
                    next.get().getBooker().getId()));
        }
        itemWithDateBookingDto.setAvailable(item.getAvailable());
        itemWithDateBookingDto.setDescription(item.getDescription());
        itemWithDateBookingDto.setName(item.getName());
        itemWithDateBookingDto.setId(item.getId());
        itemWithDateBookingDto.setComments(commentsToItemWithDateBookingComments(comments));

        return itemWithDateBookingDto;
    }

    private static ItemWithDateBooking.Comment commentToItemWithDateBookingComment(Comment comment) {
        return new ItemWithDateBooking.Comment(comment.getId(), comment.getText(),
                comment.getAuthor().getName(), comment.getCreated());
    }

    private static List<ItemWithDateBooking.Comment> commentsToItemWithDateBookingComments(List<Comment> comments) {
        return comments.stream().map(MapToItem::commentToItemWithDateBookingComment).collect(Collectors.toList());
    }

}
