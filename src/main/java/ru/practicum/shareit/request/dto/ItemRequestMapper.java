package ru.practicum.shareit.request.dto;

import lombok.NonNull;
import ru.practicum.shareit.request.model.ItemRequest;

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
}
