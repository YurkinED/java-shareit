package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemWithDateBooking;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.List;


@Slf4j
@RestController
@RequestMapping(
        value = "/items",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemWithDateBooking> getAll(@RequestHeader(value = "X-Sharer-User-Id") long user) {
        log.info("Получение всех вещей {}",user);
        return itemService.getAll(user);
    }

    @PostMapping("")
    public ItemDto create(@RequestHeader(value = "X-Sharer-User-Id") long user,
                          @Valid @RequestBody ItemDto item) {
        log.info("Добавление вещей: {}", item);
        return itemService.add(user, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(value = "X-Sharer-User-Id") long user,
                          @PathVariable("itemId") long itemId, @RequestBody ItemDto item) {
        log.info("Обновление вещей: {},{}, {}", user, itemId, item);
        return itemService.update(user, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ItemWithDateBooking get(@RequestHeader(value = "X-Sharer-User-Id") long user,@PathVariable("itemId") long itemId) {
        log.info("Получение вещей по id: {}, {}", user, itemId);
        return itemService.get(user, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(value = "X-Sharer-User-Id") long user,
                                @Valid @RequestParam @NotBlank String text) {
        log.info("Поиск вещей по фразе {}", text);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                                 @RequestBody @Valid CommentDto comment) {
        return itemService.addComment(userId, itemId, comment);
    }


}
