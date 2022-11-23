package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Component
public interface ItemStorage {
    List<Item> getAll(long user);

    List<Item> search(String searchStr);

    Item add(Item item);

    Item update(Item item);

    Item get(long itemId);

    Boolean isExistById(long itemId);
}
