package ru.practicum.shareit.exception.exp;

import lombok.Generated;
import lombok.Getter;

@Getter
@Generated
public class BadRequestException extends RuntimeException {
    private final String message;

    public BadRequestException(String message) {
        super(message);
        this.message = message;
    }
}
