package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUser_WhenUserExists_ShouldReturnUser() {
        // Given
        Long userId = 1L;
        User user = new User(userId, "John Doe", "john@example.com");
        when(userRepository.findUserById(userId)).thenReturn(user);

        // When
        UserDto result = userService.getUser(userId);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        verify(userRepository, times(2)).findUserById(userId);
    }

    @Test
    void getUser_WhenUserNotExists_ShouldThrowNotFoundException() {
        // Given
        Long userId = 999L;
        when(userRepository.findUserById(userId)).thenReturn(null);

        // When & Then
        assertThrows(NotFoundException.class, () -> userService.getUser(userId));
        verify(userRepository, times(1)).findUserById(userId);
    }

    @Test
    void createUser_WithValidData_ShouldCreateUser() {
        // Given
        UserDto userDto = new UserDto();
        userDto.setId(null);
        userDto.setName("John Doe");
        userDto.setEmail("john@example.com");
        User user = UserMapper.mapToUser(userDto);
        User savedUser = new User(1L, "John Doe", "john@example.com");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        verify(userRepository, times(1)).existsByEmail("john@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_WithDuplicateEmail_ShouldThrowDuplicateException() {

        UserDto userDto = new UserDto();
        userDto.setId(null);
        userDto.setName("John Doe");
        userDto.setEmail("john@example.com");
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(DuplicateException.class, () -> userService.createUser(userDto));
        verify(userRepository, times(1)).existsByEmail("john@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WithValidNameAndEmail_ShouldUpdateUser() {
        Long userId = 1L;
        User existingUser = new User(userId, "John Old", "john.old@example.com");
        UserDto updateDto = new UserDto();
        updateDto.setId(null);
        updateDto.setName("John New");
        updateDto.setEmail("john.new@example.com");
        User updatedUser = new User(userId, "John New", "john.new@example.com");

        when(userRepository.findUserById(userId)).thenReturn(existingUser);
        when(userRepository.findAll()).thenReturn(Arrays.asList(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.updateUser(userId, updateDto);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("John New", result.getName());
        assertEquals("john.new@example.com", result.getEmail());
        verify(userRepository, times(1)).findUserById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_WithOnlyName_ShouldUpdateOnlyName() {
        Long userId = 1L;
        User existingUser = new User(userId, "John Old", "john@example.com");
        UserDto updateDto = new UserDto();
        updateDto.setId(null);
        updateDto.setName("John New");
        updateDto.setEmail(null);
        User updatedUser = new User(userId, "John New", "john@example.com");

        when(userRepository.findUserById(userId)).thenReturn(existingUser);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.updateUser(userId, updateDto);

        assertNotNull(result);
        assertEquals("John New", result.getName());
        assertEquals("john@example.com", result.getEmail());
        verify(userRepository, times(1)).findUserById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_WithOnlyEmail_ShouldUpdateOnlyEmail() {
        // Given
        Long userId = 1L;
        User existingUser = new User(userId, "John Doe", "john.old@example.com");
        UserDto updateDto = new UserDto();
        updateDto.setId(null);
        updateDto.setName(null);
        updateDto.setEmail("john.new@example.com");
        User updatedUser = new User(userId, "John Doe", "john.new@example.com");

        when(userRepository.findUserById(userId)).thenReturn(existingUser);
        when(userRepository.findAll()).thenReturn(Arrays.asList(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        UserDto result = userService.updateUser(userId, updateDto);

        // Then
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john.new@example.com", result.getEmail());
        verify(userRepository, times(1)).findUserById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_WithDuplicateEmail_ShouldThrowDuplicateException() {
        Long userId = 1L;
        User existingUser = new User(userId, "John Doe", "john@example.com");
        User otherUser = new User(2L, "Other User", "other@example.com");
        UserDto updateDto = new UserDto();
        updateDto.setId(null);
        updateDto.setName(null);
        updateDto.setEmail("other@example.com");

        when(userRepository.findUserById(userId)).thenReturn(existingUser);
        when(userRepository.findAll()).thenReturn(Arrays.asList(existingUser, otherUser));

        assertThrows(DuplicateException.class, () -> userService.updateUser(userId, updateDto));
        verify(userRepository, times(1)).findUserById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WhenUserNotExists_ShouldThrowNotFoundException() {
        Long userId = 999L;
        UserDto updateDto = new UserDto();
        updateDto.setId(null);
        updateDto.setName("John New");
        updateDto.setEmail("john@example.com");

        when(userRepository.findUserById(userId)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userService.updateUser(userId, updateDto));
        verify(userRepository, times(1)).findUserById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteUser() {

        Long userId = 1L;
        User user = new User(userId, "John Doe", "john@example.com");
        when(userRepository.findUserById(userId)).thenReturn(user);
        doNothing().when(userRepository).delete(user);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).findUserById(userId);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUser_WhenUserNotExists_ShouldThrowNotFoundException() {

        Long userId = 999L;
        when(userRepository.findUserById(userId)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository, times(1)).findUserById(userId);
        verify(userRepository, never()).delete(any(User.class));
    }
}
