package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
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
    @Autowired
    private final ItemStorage itemStorage;
    private final UserService userService;

    public List<ItemDto> getAll(Long user){
        return itemStorage.getAll().stream().filter(x ->x.getUser()==user).map(x -> MapToItem.toDto(x)).toList();
    }

    public ItemDto add(Long user, ItemDto itemDto){
        if (!userService.isExistById(user)) {
            throw new NoUserException("Такого пользователь не существует");
        }
            Item item = MapToItem.fromDto(itemDto, user);
            return MapToItem.toDto(itemStorage.add(item));
    }

    public ItemDto update(Long user, Long itemId, ItemDto itemDto){
        System.out.println(itemDto);
        System.out.println(itemStorage.isExistById(itemId));
        System.out.println(userService.isExistById(user));
        System.out.println(itemStorage.get(itemId));
        if (!itemStorage.isExistById(itemId)){
            throw new ValidationException("Такой вещи не существует");
        }
        if (!userService.isExistById(user)) {
            throw new NoUserException("Такого пользователь не существует");
        }
        if (itemStorage.get(itemId).getUser()!=user){
            throw new NoUserException("Этот предмет принадлежит другому пользователю");
        }
        System.out.println(itemDto);
        itemDto.setId(itemId);
        System.out.println(user);
        Item item = MapToItem.fromDto(itemDto, user);
        System.out.println(item);

        return MapToItem.toDto(itemStorage.update(item));
    }

    public ItemDto get(long itemId){
        if (!itemStorage.isExistById(itemId)){
            throw new ValidationException("Такой вещи не существует");
        }
        return MapToItem.toDto(itemStorage.get(itemId));
    }

    public List<ItemDto> search(Long user, String searchText ) {
        if (searchText == null || searchText.equals("")){
            return new ArrayList<>();
        }
        return itemStorage.search(searchText).stream()
                .filter(x -> x.getAvailable() == true)
                .map(x -> MapToItem.toDto(x)).toList();    }

}
