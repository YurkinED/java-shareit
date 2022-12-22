package ru.practicum.shareit.request.service;

import java.util.List;
import ru.practicum.shareit.request.dto.ItemRequestDto;

public interface ItemRequestService {

  ItemRequestDto createItemRequest(long requesterId, ItemRequestDto requestDto);

  List<ItemRequestDto> getUserItemRequests(long userId);

  List<ItemRequestDto> getItemRequests(long userId, Integer from, Integer size);

  ItemRequestDto getItemRequest(long userId, long requestId);
}
