package ru.practicum.shareit.user.service;


import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public interface UserService {

    UserDto getUser(Long id);

    UserDto createUser(User user);

    UserDto updateUser(Long userId, User user);

    void deleteUser(long id);

}
