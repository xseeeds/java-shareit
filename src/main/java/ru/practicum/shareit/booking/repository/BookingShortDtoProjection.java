package ru.practicum.shareit.booking.repository;

import java.time.LocalDateTime;

public interface BookingShortDtoProjection {

    Long getId();

    Long getBookerId();

    LocalDateTime getStart();

    LocalDateTime getEnd();

}