package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;


@Component
public interface UserStorage {

    List<User> getAll();

    User add(User user);

    User update(User user);

    User get(Optional<Long> userId);

    User delete(long userId);

    Boolean isExistByEmail(User user);

    Boolean isExistById(Long userId);
}
