package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
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
        User user = userRepository.getUser(id);
        return UserMapper.mapToUser(user);
    }

    @Override
    public UserDto createUser(User user) {
        validateUser(user);
        validateEmail(user);
        log.info("Создали user");
        return UserMapper.mapToUser(userRepository.createUser(user));
    }

    @Override
    public UserDto updateUser(Long userId, User user) {
        if (user.getEmail() != null) {
            if (!user.getEmail().contains("@")) {
                throw new ValidationException("Имя почты указано не корректно!");
            }
            validateEmail(user);
        }
        log.info("Обновили user");
        return UserMapper.mapToUser(userRepository.updateUser(userId, user));
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteUser(id);
    }

    private void validateEmail(User user) {
        if (user.getEmail() != null) {
            boolean containsEmail = userRepository.findUsers().stream()
                    .anyMatch(existingUser -> existingUser.getEmail().equals(user.getEmail()) && !existingUser.getId().equals(user.getId()));
            if (containsEmail) {
                throw new DuplicateException("Указанная почта уже зарегистрирована!");
            }
        }
    }

    private void validateUser(User user) {
        if (user.getName() == null) {
            throw new ValidationException("Имя пользователя не указано");
        }
        if (user.getEmail() == null) {
            throw new ValidationException("Почта пользователя не указана");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Имя почты указано не корректно!");
        }
    }
}
