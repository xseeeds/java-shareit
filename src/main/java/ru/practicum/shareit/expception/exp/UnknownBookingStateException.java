package ru.practicum.shareit.expception.exp;

import lombok.Getter;

@Getter
public class UnknownBookingStateException extends RuntimeException {
    private final String message;

    public UnknownBookingStateException(String message) {
        super(message);
        this.message = message;
    }

}