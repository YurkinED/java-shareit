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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    public List<ItemDto> getAll(long user) {
        return itemStorage.getAll(user).stream().map(x -> MapToItem.toDto(x)).collect(Collectors.toList());
    }

    public ItemDto add(long user, ItemDto itemDto) {
        if (!userService.isExistById(user)) {
            throw new NoItemUserException("Такого пользователь не существует");
        }
        Item item = MapToItem.fromDto(itemDto, user);
        return MapToItem.toDto(itemStorage.add(item));
    }

    public ItemDto update(long user, long itemId, ItemDto itemDto) {
        if (!itemStorage.isExistById(itemId)) {
            throw new ValidationException("Такой вещи не существует");
        }
        if (!userService.isExistById(user)) {
            throw new NoUserException("Такого пользователь не существует");
        }
        if (itemStorage.get(Optional.of(itemId)).getUser() != user) {
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
        return MapToItem.toDto(itemStorage.get(Optional.of(itemId)));
    }

    public List<ItemDto> search(String searchText) {
        return itemStorage.search(searchText).stream()
                .map(x -> MapToItem.toDto(x)).collect(Collectors.toList());

    }

}
