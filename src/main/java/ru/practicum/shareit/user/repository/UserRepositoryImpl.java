package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final List<User> users = new ArrayList<>();

    @Override
    public User getUser(long id) {
        User user = findUser(id);
        if (user == null) {
            log.info("Пользователь под id: {} не найден!", id);
            throw new NotFoundException("User not found");
        }
        return user;
    }

    @Override
    public User createUser(User user) {
        User newUser = new User();
        newUser.setId(getId());
        newUser.setName(user.getName());
        newUser.setEmail(user.getEmail());
        users.add(newUser);
        return newUser;
    }

    @Override
    public User updateUser(Long userId, User user) {
        User updateUser = findUser(userId);
        if (updateUser == null) {
            log.info("Пользователь под id: {} не найден!", user.getId());
            throw new NotFoundException("User not found");
        }
        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updateUser.setEmail(user.getEmail());
        }

        return updateUser;
    }

    @Override
    public void deleteUser(long id) {
        User user = findUser(id);
        if (user == null) {
            log.info("Пользователь под id: {} не найден!", id);
            throw new NotFoundException("User not found");
        }
        users.remove(user);
    }

    @Override
    public List<User> findUsers() {
        return users;
    }

    private Long getId() {
        long userId = users.stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0L);
        return userId + 1;
    }

    private User findUser(long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

}
