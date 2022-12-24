package ru.practicum.shareit.request.service;

import java.time.LocalDateTime;
import java.util.*;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.UserService;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final RequestRepository requestRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public ItemRequestDto createItemRequest(long requesterId, ItemRequestDto requestDto) {
        userService.get(requesterId);
        requestDto.setCreated(LocalDateTime.now());
        var itemRequest = requestRepository.save(ItemRequestMapper.toItemRequest(requesterId, requestDto));
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getUserItemRequests(long userId) {
        userService.get(userId);
        List<ItemRequest> itemRequests = requestRepository.findAllByRequesterId(userId, Sort.by("createDateTime").descending());
        Map<Long, List<ItemDto>> items = itemService.findAll().stream().filter(r -> r.getRequestId() != null)
                .collect(groupingBy(ItemDto::getRequestId, toList()));

        List<ItemRequestDto> itemRequestDto = new ArrayList<>();

        for (ItemRequest itemRequest : itemRequests) {

            itemRequestDto.add(ItemRequestMapper.toItemRequestDtoWithItems(itemRequest,
                    items.getOrDefault(itemRequest.getId(), Collections.emptyList())));
        }
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getItemRequests(long userId, Integer from, Integer size) {
        userService.get(userId);
        Sort sort = Sort.by("createDateTime").descending();
        Pageable pageable = from != null && size != null
                ? PageRequest.of(from / size, size, sort)
                : PageRequest.of(0, Integer.MAX_VALUE, sort);

        var itemRequests = requestRepository.findAllByRequesterIdNot(userId, pageable);
        Map<Long, List<ItemDto>> items = itemService.findAll().stream().filter(r -> r.getRequestId() != null)
                .collect(groupingBy(ItemDto::getRequestId, toList()));
        List<ItemRequestDto> itemRequestDto = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {

            itemRequestDto.add(ItemRequestMapper.toItemRequestDtoWithItems(itemRequest,
                    items.getOrDefault(itemRequest.getId(), Collections.emptyList())));
        }
        return itemRequestDto;
    }

    @Override
    public ItemRequestDto getItemRequest(long userId, long requestId) {
        userService.get(userId);
        var itemRequestDto = ItemRequestMapper.toItemRequestDto(
                requestRepository.findById(requestId)
                        .orElseThrow(() -> {
                            throw new NoSuchElementException("Запроса не найдено");
                        }));
        itemRequestDto.setItems(itemService.findItemByRequestId(itemRequestDto.getId()));
        return itemRequestDto;
    }
}
