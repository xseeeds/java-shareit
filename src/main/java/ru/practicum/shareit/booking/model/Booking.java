package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.user.model.UserEntity;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Value
@Builder(toBuilder = true)
public class Booking {
    long id;
    LocalDateTime start;
    LocalDateTime end;
    ItemEntity itemEntity;
    UserEntity booker;
    Status status;
}
