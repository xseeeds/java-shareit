package ru.practicum.shareit.expception.exp;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
    private final String message;

    public BadRequestException(String message) {
        super(message);
        this.message = message;
    }
}
