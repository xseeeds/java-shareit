package ru.practicum.shareit.exception.exp;

import lombok.Generated;
import lombok.Getter;

@Getter
@Generated
public class ConflictException extends RuntimeException {
    private final String message;

    public ConflictException(String message) {
        super(message);
        this.message = message;
    }
}