package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CommentDto implements Serializable {
    private long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}