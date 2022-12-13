package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NoUserException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.MapToUser;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userStorage;

    public List<UserDto> getAll() {
        return userStorage.findAll().stream().map(x -> MapToUser.toDto(x)).collect(Collectors.toList());
    }

    @Transactional
    public UserDto add(UserDto userDto) {
        User user = userStorage.save(MapToUser.fromDto(userDto));
        return MapToUser.toDto(user);
    }

    @Transactional
    public UserDto update(Long userId, UserDto userDto) {
        User userUpdate = userStorage.findById(userId).orElseThrow(() -> {
            throw new NoUserException("Такого пользователь не существует");
        });
        userDto.setId(userId);
        User user = MapToUser.fromDto(userDto);
        if (user.getName() != null && !user.getName().isBlank()) {
            userUpdate.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            userUpdate.setEmail(user.getEmail());
        }
        return MapToUser.toDto(userUpdate);
    }

    public UserDto get(long userId) {
        return MapToUser.toDto(userStorage.findById(userId).orElseThrow(() -> {
            throw new NoUserException("Такого пользователь не существует");
        }));
    }

    @Transactional
    public UserDto delete(long userId) {
        User user = userStorage.findById(userId).orElseThrow(() -> {
            throw new NoUserException("Такого пользователь не существует");
        });
        userStorage.deleteById(userId);
        return MapToUser.toDto(user);
    }

}
