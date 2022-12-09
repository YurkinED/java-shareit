package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository  extends JpaRepository<Item, Long> {

    @Query(value = "select * from items where user_id= :userData", nativeQuery = true)
    List<Item> getAllByUser(Long userData);

    @Query(value = "select * from items where available = true and (upper(name) like upper(:str) or upper(description) like upper(:str))", nativeQuery = true)
    List<Item> search(String str);

    List<Item> findAllByUser(long userId);
}
