package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserStorage userStorage;

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User add(User user) {
        if (userStorage.isExistByEmail(user)) {
            throw new ValidationException("Такой Email зарегестрирован");
        }
        return userStorage.add(user);
    }

    public User update(Long userId, User user) {
        if (!userStorage.isExistById(userId)) {
            throw new ValidationException("Такого пользователь не существует");
        }
        user.setId(userId);
        if (userStorage.isExistByEmail(user)) {
            throw new ValidationException("Этот email занят");
        }
        return userStorage.update(user);
    }

    public User get(long userId) {
        if (!userStorage.isExistById(userId)) {
            throw new ValidationException("Такого пользователь не существует");
        }
        return userStorage.get(userId);
    }

    public boolean isExistById(long userId) {
        return userStorage.isExistById(userId);
    }

    public User delete(long userId) {
        if (!userStorage.isExistById(userId)) {
            throw new ValidationException("Такого пользователь не существует");
        }
        return userStorage.delete(userId);
    }

}
