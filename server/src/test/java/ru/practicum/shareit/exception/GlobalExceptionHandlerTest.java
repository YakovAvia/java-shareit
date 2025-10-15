package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.dto.ErrorDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleValidationException() {
        ValidationException exception = new ValidationException("Validation error");
        ErrorDto errorDto = handler.handleValidationException(exception);
        assertEquals("Validation error", errorDto.getMessage());
    }

    @Test
    void handleNotFoundException() {
        NotFoundException exception = new NotFoundException("Not found error");
        ErrorDto errorDto = handler.handleNotFoundException(exception);
        assertEquals("Not found error", errorDto.getMessage());
    }

    @Test
    void handleDuplicateException() {
        DuplicateException exception = new DuplicateException("Duplicate error");
        ErrorDto errorDto = handler.handleDuplicateException(exception);
        assertEquals("Duplicate error", errorDto.getMessage());
    }
}
