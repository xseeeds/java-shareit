package ru.practicum.shareit.validation;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.exception.exp.UnknownBookingStateException;

@UtilityClass
public class Converter {

    public State getState(String state) {
        final State s;
        try {
            s = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnknownBookingStateException("Unknown state: " + state);
        }
        return s;
    }

    public static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
}
