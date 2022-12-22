package ru.practicum.shareit.request;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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
@Validated
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestController {

  private final ItemRequestService itemRequestService;

  @PostMapping
  public ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") long requesterId,
      @RequestBody @Valid ItemRequestDto itemRequestDto) {
    return itemRequestService.createItemRequest(requesterId, itemRequestDto);
  }

  @GetMapping
  public List<ItemRequestDto> getUserItemRequests(@RequestHeader("X-Sharer-User-Id") long requesterId) {
    return itemRequestService.getUserItemRequests(requesterId);
  }

  @GetMapping("/all")
  public List<ItemRequestDto> getItemRequests(@RequestHeader("X-Sharer-User-Id") long requesterId,
      @RequestParam(value = "from", required = false) @PositiveOrZero Integer from,
      @RequestParam(value = "size", required = false) @PositiveOrZero Integer size) {
    return itemRequestService.getItemRequests(requesterId, from, size);
  }

  @GetMapping("/{requestId}")
  public ItemRequestDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") long requesterId,
      @PathVariable long requestId) {
    return itemRequestService.getItemRequest(requesterId, requestId);
  }
}
