package ru.practicum.shareit.expception.exp;

import lombok.Generated;
import lombok.Getter;

@Getter
@Generated
public class NotFoundException extends RuntimeException {
    private final String message;

    public NotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
