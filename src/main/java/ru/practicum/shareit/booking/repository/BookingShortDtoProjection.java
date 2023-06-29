package ru.practicum.shareit.booking.repository;


import lombok.Generated;

import java.time.LocalDateTime;

@Generated
public interface BookingShortDtoProjection {

    Long getId();

    Long getBookerId();

    LocalDateTime getStart();

    LocalDateTime getEnd();

}