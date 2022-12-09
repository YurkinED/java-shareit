package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingResponseDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private String status;

    @Data
    public static class Item {
        private long id;
        private String name;

        public Item(long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Data
    public static class User {


        private long id;
        private String name;

        public User(long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}