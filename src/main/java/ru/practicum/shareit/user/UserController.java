package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(
        value = "/users",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Получение всех пользователей");
        return userService.getAll();
    }

    @PostMapping("")
    public UserDto create(@Validated(Create.class) @RequestBody UserDto user) {
        log.info("Добавление пользователя: {}", user);
        return userService.add(user);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable("userId") long userId,
                          @Validated(Update.class) @RequestBody UserDto user) {
        log.info("Обновление пользователя:{}, {}", userId, user);
        return userService.update(userId, user);
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable("userId") long userId) {
        log.info("Получение пользователя:{}", userId);
        return userService.get(userId);
    }

    @DeleteMapping("/{userId}")
    public UserDto delete(@PathVariable("userId") long userId) {
        log.info("Обновление пользователя:{}", userId);
        return userService.delete(userId);
    }


}
