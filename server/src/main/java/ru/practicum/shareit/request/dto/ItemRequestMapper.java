package ru.practicum.shareit.request.dto;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@UtilityClass
public class ItemRequestMapper {

    public static ItemRequest toItemRequest(long requesterId, @NonNull ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requesterId(requesterId)
                .createDateTime(itemRequestDto.getCreated())
                .build();
    }

    public static ItemRequestDto toItemRequestDto(@NonNull ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreateDateTime())
                .build();
    }

    public static ItemRequestDto toItemRequestDtoWithItems(@NonNull ItemRequest itemRequest, List<ItemDto> itemList) {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreateDateTime())
                .build();
        itemRequestDto.setItems(itemList);

        return itemRequestDto;

    }
}
