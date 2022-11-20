package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class MapToItem {
    public static Item fromDto(ItemDto itemDto, long user) {
        Item item=Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .user(user)
                .build();

        return item;
    }

    public static ItemDto toDto(Item item) {
        ItemDto itemDto=ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();

        return itemDto;
    }
}
