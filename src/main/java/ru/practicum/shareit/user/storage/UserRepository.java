package ru.practicum.shareit.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.model.User;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "select * from shareit.users where email=:emailData", nativeQuery = true)
    List<User> isExistByEmail(String emailData);
}
