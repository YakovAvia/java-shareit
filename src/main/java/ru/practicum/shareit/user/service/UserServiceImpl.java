package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        if (userRepository.findUserById(id) == null) {
            log.info("Пользователь под id: {} не найден!", id);
            throw new NotFoundException("Пользователь не найден!");
        }
        User user = userRepository.findUserById(id);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail() != null) {
            boolean containsEmail = userRepository.findAll().stream()
                    .anyMatch(existingUser -> existingUser.getEmail().equals(userDto.getEmail()));
            if (containsEmail) {
                throw new DuplicateException("Указанная почта уже зарегистрирована!");
            }
        }
        log.info("Пользователь с ID: {} успешно создан!", userDto.getId());
        return UserMapper.mapToUserDto(userRepository.save(UserMapper.mapToUser(userDto)));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userRepository.findUserById(userId);

        if (user == null) {
            log.info("Пользователь не найден!");
            throw new NotFoundException("Пользователь не найден!");
        }

        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            if (!user.getEmail().contains("@")) {
                throw new ValidationException("Имя почты указано не корректно!");
            }
            boolean containsEmail = userRepository.findAll().stream()
                    .anyMatch(existingUser -> existingUser.getEmail().equals(userDto.getEmail()));
            if (containsEmail) {
                throw new DuplicateException("Указанная почта уже зарегистрирована!");
            }
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        log.info("Пользователь с ID: {} успешно обновлен!",userId);
        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(long id) {
        User user = userRepository.findUserById(id);
        if (user == null) {
            log.info("Пользователь под id: {} не найден!", id);
            throw new NotFoundException("Пользователь не найден!");
        }
        userRepository.delete(user);
    }

}
