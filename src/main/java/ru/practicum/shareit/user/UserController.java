package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
    public List<User> getAll() {
        log.info("Получение всех пользователей");
        return userService.getAll();
    }

    @PostMapping("")
    public User createUser(@Valid @RequestBody User user) {
        log.info("Добавление пользователя: {}", user);
        userService.add(user);
        return user;
    }

    @PatchMapping("/{userId}")
    public User createUser(@PathVariable("userId") long userId, @RequestBody User user) {
        log.info("Обновление пользователя:{}, {}",userId,  user);
        return userService.update(userId, user);
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable("userId") long userId) {
        log.info("Получение пользователя:{}",userId);
        return userService.get(userId);
    }

    @DeleteMapping("/{userId}")
    public User deleteUser(@PathVariable("userId") long userId) {
        log.info("Обновление пользователя:{}",userId);
        return userService.delete(userId);
    }


}
