package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NoItemUserException;
import ru.practicum.shareit.exceptions.NoUserException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.MapToItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    public List<ItemDto> getAll(long user) {
        List<ItemDto> list = new ArrayList<>();
        for (Item x : itemStorage.getAll()) {
            if (x.getUser() == user) {
                ItemDto itemDto = MapToItem.toDto(x);
                list.add(itemDto);
            }
        }
        return list;
    }

    public ItemDto add(long user, ItemDto itemDto) {
        if (!userService.isExistById(user)) {
            throw new NoItemUserException("Такого пользователь не существует");
        }
        Item item = MapToItem.fromDto(itemDto, user);
        return MapToItem.toDto(itemStorage.add(item));
    }

    public ItemDto update(long user, long itemId, ItemDto itemDto) {
        System.out.println(itemDto);
        if (!itemStorage.isExistById(itemId)) {
            throw new ValidationException("Такой вещи не существует");
        }
        if (!userService.isExistById(user)) {
            throw new NoUserException("Такого пользователь не существует");
        }
        if (itemStorage.get(itemId).getUser() != user) {
            throw new NoItemUserException("Этот предмет принадлежит другому пользователю");
        }
        itemDto.setId(itemId);
        Item item = MapToItem.fromDto(itemDto, user);
        return MapToItem.toDto(itemStorage.update(item));
    }

    public ItemDto get(long itemId) {
        if (!itemStorage.isExistById(itemId)) {
            throw new ValidationException("Такой вещи не существует");
        }
        return MapToItem.toDto(itemStorage.get(itemId));
    }

    public List<ItemDto> search(String searchText) {
        List<ItemDto> list = new ArrayList<>();
        for (Item x : itemStorage.search(searchText)) {
                list.add(MapToItem.toDto(x));
        }
        return list;
    }

}
