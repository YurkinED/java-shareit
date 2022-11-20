package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@RequestMapping(
        value = "/items",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ItemController {
    @Autowired
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader(value = "X-Sharer-User-Id", required = true) long user) {
        log.info("Получение всех вещей");
        return itemService.getAll(user);
    }

    @PostMapping("")
    public ItemDto createItem(@RequestHeader(value = "X-Sharer-User-Id", required = true) long user, @Valid @RequestBody ItemDto item) {
        log.info("Добавление вещей: {}", item);
        return itemService.add(user, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(value = "X-Sharer-User-Id", required = true) long user, @PathVariable("itemId") long itemId, @RequestBody ItemDto item) {
        log.info("Обновление вещей: {},{}, {}", user, itemId,  item);
        return itemService.update(user, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDto getUser(@PathVariable("itemId") long itemId) {
        log.info("Получение вещей по id: {}",itemId);
        return itemService.get(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(value = "X-Sharer-User-Id", required = true) long user, @RequestParam(required = true) String text) {
        log.info("Поиск вещей по фразе {}", text);
        return itemService.search(user, text);
    }





}
