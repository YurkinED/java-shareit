package ru.practicum.shareit.request.service;

import java.util.List;
import ru.practicum.shareit.request.dto.ItemRequestDto;

public interface ItemRequestService {

  ItemRequestDto create(long requesterId, ItemRequestDto requestDto);

  List<ItemRequestDto> getByUser(long userId);

  List<ItemRequestDto> getList(long userId, Integer from, Integer size);

  ItemRequestDto get(long userId, long requestId);
}
