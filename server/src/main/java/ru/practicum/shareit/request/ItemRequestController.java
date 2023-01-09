package ru.practicum.shareit.request;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestController {

  private final ItemRequestService itemRequestService;

  @PostMapping
  public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") long requesterId,
                               @RequestBody ItemRequestDto itemRequestDto) {
    return itemRequestService.create(requesterId, itemRequestDto);
  }

  @GetMapping
  public List<ItemRequestDto> getByUser(@RequestHeader("X-Sharer-User-Id") long requesterId) {
    return itemRequestService.getByUser(requesterId);
  }

  @GetMapping("/all")
  public List<ItemRequestDto> get(@RequestHeader("X-Sharer-User-Id") long requesterId,
                                  @RequestParam(value = "from", defaultValue = "0")  Integer from,
                                  @RequestParam(value = "size", defaultValue = "1000")  Integer size) {
    return itemRequestService.getList(requesterId, from, size);
  }

  @GetMapping("/{requestId}")
  public ItemRequestDto getById(@RequestHeader("X-Sharer-User-Id") long requesterId,
                                @PathVariable long requestId) {
    return itemRequestService.get(requesterId, requestId);
  }
}
