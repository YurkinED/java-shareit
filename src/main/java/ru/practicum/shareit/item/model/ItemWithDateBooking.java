package ru.practicum.shareit.item.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemWithDateBooking {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<Comment> comments;

    @Data
    public static class Booking {
        private long id;
        private long bookerId;

        public Booking(long id, long bookerId) {
            this.id = id;
            this.bookerId = bookerId;
        }
    }

    @Data
    public static class Comment {
        private long id;
        private String text;
        private String authorName;
        private LocalDateTime created;

        public Comment(long id, String text, String authorName, LocalDateTime created) {
            this.id = id;
            this.text = text;
            this.authorName = authorName;
            this.created = created;
        }
    }
}