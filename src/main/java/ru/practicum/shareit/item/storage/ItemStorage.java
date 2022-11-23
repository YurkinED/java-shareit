package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Component
public interface ItemStorage {
    List<Item> getAll(long user);

    List<Item> search(String searchStr);

    Item add(Item item);

    Item update(Item item);

    Item get(Optional<Long> itemId);

    Boolean isExistById(long itemId);
}
