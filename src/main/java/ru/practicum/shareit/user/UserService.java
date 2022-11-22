package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.MapToUser;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public List<UserDto> getAll() {
        List<UserDto> list = new ArrayList<>();
        for (User user : userStorage.getAll()) {
            list.add(MapToUser.toDto(user));
        }
        return list;
    }

    public UserDto add(UserDto user) {
        if (userStorage.isExistByEmail(MapToUser.fromDto(user))) {
            throw new ValidationException("Такой Email зарегестрирован");
        }
        return MapToUser.toDto(userStorage.add(MapToUser.fromDto(user)));
    }

    public UserDto update(Long userId, UserDto user) {
        if (!userStorage.isExistById(userId)) {
            throw new ValidationException("Такого пользователь не существует");
        }
        user.setId(userId);
        if (userStorage.isExistByEmail(MapToUser.fromDto(user))) {
            throw new ValidationException("Этот email занят");
        }
        return MapToUser.toDto(userStorage.update(MapToUser.fromDto(user)));
    }

    public UserDto get(long userId) {
        if (!userStorage.isExistById(userId)) {
            throw new ValidationException("Такого пользователь не существует");
        }
        return MapToUser.toDto(userStorage.get(userId));
    }

    public boolean isExistById(long userId) {
        return userStorage.isExistById(userId);
    }

    public UserDto delete(long userId) {
        if (!userStorage.isExistById(userId)) {
            throw new ValidationException("Такого пользователь не существует");
        }
        return MapToUser.toDto(userStorage.delete(userId));
    }

}
