package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {

    protected Map<Long, Item> items = new HashMap<>();
    private int id = 0;

    @Override
    public List<Item> getAll(long user) {
        return items.values().stream().filter(x -> x.getUser() == user).collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String searchStr) {
        List<Item> itemList = new ArrayList<>();
        for (Item item : items.values()) {
            if ((item.getName().toUpperCase(Locale.ROOT)
                    .contains(searchStr.toUpperCase(Locale.ROOT))
                    ||
                    item.getDescription().toUpperCase(Locale.ROOT)
                            .contains(searchStr.toUpperCase(Locale.ROOT)))
                    && item.getAvailable()
            ) {
                itemList.add(item);
            }
        }
        return itemList;
    }

    @Override
    public Item add(Item item) {
        item.setId(++this.id);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Boolean isExistById(long userId) {
        return items.containsKey(userId);
    }

    @Override
    public Item update(Item item) {
        Item itemObj = items.get(item.getId());
        itemObj = Item.builder()
                .id(item.getId())
                .name(item.getName() != null && !item.getName().isBlank() ?
                        item.getName() : itemObj.getName())
                .description(item.getDescription() != null  && !item.getDescription().isBlank() ?
                        item.getDescription() : itemObj.getDescription())
                .available(item.getAvailable() != null ? item.getAvailable() : itemObj.getAvailable())
                .user(item.getUser() != null ? item.getUser() : itemObj.getUser())
                .build();
        items.put(itemObj.getId(), itemObj);
        return itemObj;
    }

    @Override
    public Item get(long userId) {
        return items.get(userId);
    }

}
