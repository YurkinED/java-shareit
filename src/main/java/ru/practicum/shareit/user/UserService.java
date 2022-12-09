package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NoUserException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.MapToUser;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    // private final UserStorage userStorage;
    private final UserRepository userStorage;

    public List<UserDto> getAll() {
        List<UserDto> list = new ArrayList<>();
        for (User user : userStorage.findAll()) {
            list.add(MapToUser.toDto(user));
        }
        return list;
    }

    public UserDto add(UserDto userDto) {
      //  if (userStorage.isExistByEmail(MapToUser.fromDto(userDto).getEmail()).size() > 0) {
      //      throw new ValidationException("Такой Email зарегестрирован");
       // }
        User user = userStorage.save(MapToUser.fromDto(userDto));
        return MapToUser.toDto(user);
    }

    public UserDto update(Long userId, UserDto userDto) {
        if (!userStorage.existsById(userId)) {
            throw new ValidationException("Такого пользователь не существует");
        }
        userDto.setId(userId);
        User user = MapToUser.fromDto(userDto);
        User userUpdate = userStorage.getById(user.getId());
        if (!userUpdate.getName().equals(user.getName()) && user.getName() != null) {
            userUpdate.setName(user.getName());
        }
        if (!userUpdate.getEmail().equals(user.getEmail()) && user.getEmail() != null) {
            userUpdate.setEmail(user.getEmail());
        }
        userUpdate = userStorage.save(userUpdate);
        return MapToUser.toDto(userUpdate);
    }

    public UserDto get(long userId) {
        if (!userStorage.existsById(userId)) {
            throw new NoUserException("Такого пользователь не существует");
        }
        return MapToUser.toDto(userStorage.getById(userId));
    }

    public boolean isExistById(long userId) {
        return userStorage.existsById(userId);
    }

    public UserDto delete(long userId) {
        if (!userStorage.existsById(userId)) {
            throw new ValidationException("Такого пользователь не существует");
        }
        User user = userStorage.getById(userId);
        userStorage.deleteById(userId);
        return MapToUser.toDto(user);
    }

}
