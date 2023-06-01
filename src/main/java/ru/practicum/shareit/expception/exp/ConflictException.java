package ru.practicum.shareit.expception.exp;

import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {
    private final String message;

    public ConflictException(String message) {
        super(message);
        this.message = message;
    }
}