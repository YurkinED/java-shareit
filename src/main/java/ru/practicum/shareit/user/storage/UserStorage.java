package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.List;


@Component
public interface UserStorage {

    List<User> getAll();

    User add(User user);

    User update(User user);

    User get(long userId);

    User delete(long userId);

    Boolean isExistByEmail(User user);

    Boolean isExistById(Long userId);
}
