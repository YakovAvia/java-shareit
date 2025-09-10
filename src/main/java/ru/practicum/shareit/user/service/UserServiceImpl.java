package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto getUser(Long id) {
        if (userRepository.findUser(id) == null) {
            log.info("Пользователь под id: {} не найден!", id);
            throw new NotFoundException("Пользователь не найден!");
        }
        User user = userRepository.getUser(id);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail() != null) {
            boolean containsEmail = userRepository.findUsers().stream()
                    .anyMatch(existingUser -> existingUser.getEmail().equals(userDto.getEmail()));
            if (containsEmail) {
                throw new DuplicateException("Указанная почта уже зарегистрирована!");
            }
        }
        User newUser = userRepository.createUser(UserMapper.mapToUser(userDto));
        return UserMapper.mapToUserDto(newUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userRepository.findUser(userId);
        if (user == null) {
            log.info("Пользователь не найден!");
            throw new NotFoundException("Пользователь не найден!");
        }
        if (user.getEmail() != null) {
            if (!user.getEmail().contains("@")) {
                throw new ValidationException("Имя почты указано не корректно!");
            }
            if (userDto.getEmail() != null) {
                boolean containsEmail = userRepository.findUsers().stream()
                        .anyMatch(existingUser -> existingUser.getEmail().equals(userDto.getEmail()));
                if (containsEmail) {
                    throw new DuplicateException("Указанная почта уже зарегистрирована!");
                }
            }
        }

        UserMapper.mapToUserUpdate(user, userDto);
        return UserMapper.mapToUserDto(userRepository.updateUser(user));
    }

    @Override
    public void deleteUser(long id) {
        User user = userRepository.findUser(id);
        if (user == null) {
            log.info("Пользователь под id: {} не найден!", id);
            throw new NotFoundException("Пользователь не найден!");
        }
        userRepository.deleteUser(user.getId());
    }

}
