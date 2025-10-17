package ru.practicum.shareit.user.service;


import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    UserDto getUser(Long id);

    UserDto createUser(UserDto userDto);

    UserDto updateUser(Long userId, UserDto userDto);

    void deleteUser(long id);

}
