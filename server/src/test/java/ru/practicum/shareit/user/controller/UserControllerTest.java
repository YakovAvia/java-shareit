package ru.practicum.shareit.user.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getUser_WhenUserExists_ShouldReturnUser() {
        Long userId = 1L;
        UserDto expectedUser = new UserDto();
        expectedUser.setId(userId);
        expectedUser.setName("John Doe");
        expectedUser.setEmail("john@example.com");
        when(userService.getUser(userId)).thenReturn(expectedUser);

        ResponseEntity<UserDto> response = userController.getUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedUser, response.getBody());
        verify(userService, times(1)).getUser(userId);
    }

    @Test
    void getUser_WhenUserNotExists_ShouldThrowException() {

        Long userId = 999L;
        when(userService.getUser(userId)).thenThrow(new NotFoundException("Пользователь не найден!"));

        assertThrows(NotFoundException.class, () -> userController.getUser(userId));
        verify(userService, times(1)).getUser(userId);
    }

    @Test
    void createUser_WithValidData_ShouldReturnCreatedUser() {
        UserDto inputUser = new UserDto();
        inputUser.setId(null);
        inputUser.setName("John Doe");
        inputUser.setEmail("john@example.com");
        UserDto createdUser = new UserDto();
        createdUser.setId(1L);
        createdUser.setName("John Doe");
        createdUser.setEmail("john@example.com");
        when(userService.createUser(any(UserDto.class))).thenReturn(createdUser);

        ResponseEntity<UserDto> response = userController.createUser(inputUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdUser, response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(userService, times(1)).createUser(inputUser);
    }

    @Test
    void updateUser_WithValidData_ShouldReturnUpdatedUser() {

        Long userId = 1L;
        UserDto updateUser = new UserDto();
        updateUser.setId(null);
        updateUser.setName("John Updated");
        updateUser.setEmail("john.updated@example.com");
        UserDto updatedUser = new UserDto();
        updatedUser.setId(userId);
        updatedUser.setName("John Updated");
        updatedUser.setEmail("john.updated@example.com");
        when(userService.updateUser(anyLong(), any(UserDto.class))).thenReturn(updatedUser);

        ResponseEntity<UserDto> response = userController.updateUser(userId, updateUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updatedUser, response.getBody());
        assertEquals("John Updated", response.getBody().getName());
        verify(userService, times(1)).updateUser(userId, updateUser);
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteSuccessfully() {
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        userController.deleteUser(userId);

        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    void deleteUser_WhenUserNotExists_ShouldThrowException() {

        Long userId = 999L;
        doThrow(new NotFoundException("Пользователь не найден!"))
                .when(userService).deleteUser(userId);

        assertThrows(NotFoundException.class, () -> userController.deleteUser(userId));
        verify(userService, times(1)).deleteUser(userId);
    }
}
