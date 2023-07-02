package ru.practicum.shareit.exception.exp;

import lombok.Generated;
import lombok.Getter;

@Getter
@Generated
public class UnknownBookingStateException extends RuntimeException {
    private final String message;

    public UnknownBookingStateException(String message) {
        super(message);
        this.message = message;
    }

}