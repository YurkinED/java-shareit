package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(
        value = "/users",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Получение всех пользователей");
        return userService.getAll();
    }

    @PostMapping("")
    public UserDto createUser(@Valid @RequestBody UserDto user) {
        log.info("Добавление пользователя: {}", user);
        return userService.add(user);
    }

    @PatchMapping("/{userId}")
    public UserDto createUser(@PathVariable("userId") long userId, @RequestBody UserDto user) {
        log.info("Обновление пользователя:{}, {}", userId, user);
        return userService.update(userId, user);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable("userId") long userId) {
        log.info("Получение пользователя:{}", userId);
        return userService.get(userId);
    }

    @DeleteMapping("/{userId}")
    public UserDto deleteUser(@PathVariable("userId") long userId) {
        log.info("Обновление пользователя:{}", userId);
        return userService.delete(userId);
    }


}
