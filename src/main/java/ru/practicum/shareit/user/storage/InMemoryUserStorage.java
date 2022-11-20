package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {

    protected Map<Long, User> users = new HashMap<>();
    private int id = 0;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User add(User user) {
        user.setId(++this.id);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Boolean isExistByEmail(User user) {
        if (user.getEmail() == null) {
            return false;
        }
        for (User userI : users.values()) {
            if (userI.getEmail().toUpperCase(Locale.ROOT).equals(user.getEmail().toUpperCase(Locale.ROOT))
                    && userI.getId() != user.getId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean isExistById(Long userId) {
        return users.containsKey(userId);
    }

    @Override
    public User update(User user) {
        User userObj = users.get(user.getId());
        userObj = User.builder()
                .id(user.getId())
                .email(user.getEmail() != null ? user.getEmail() : userObj.getEmail())
                .name(user.getName() != null ? user.getName() : userObj.getName())
                .build();
        users.put(userObj.getId(), userObj);
        return userObj;
    }

    @Override
    public User get(long userId) {
        return users.get(userId);
    }

    @Override
    public User delete(long userId) {
        User user = users.get(userId);
        users.remove(userId);
        return user;
    }

}
