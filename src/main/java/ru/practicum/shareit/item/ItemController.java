package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
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
    public List<ItemDto> getAll(@RequestHeader(value = "X-Sharer-User-Id") long user) {
        log.info("Получение всех вещей");
        return itemService.getAll(user);
    }

    @PostMapping("")
    public ItemDto create(@RequestHeader(value = "X-Sharer-User-Id") long user, @Valid @RequestBody ItemDto item) {
        log.info("Добавление вещей: {}", item);
        return itemService.add(user, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(value = "X-Sharer-User-Id") long user, @PathVariable("itemId") long itemId, @RequestBody ItemDto item) {
        log.info("Обновление вещей: {},{}, {}", user, itemId, item);
        return itemService.update(user, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable("itemId") long itemId) {
        log.info("Получение вещей по id: {}", itemId);
        return itemService.get(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(value = "X-Sharer-User-Id") long user, @Validated @RequestParam(required = true) @NotEmpty String text) {
        log.info("Поиск вещей по фразе {}", text);
        return itemService.search(text);
    }


}
