package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    User getUser(long id);

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(long id);

    List<User> findUsers();

    User findUser(long id);

}
