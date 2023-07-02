package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.enums.Status;

import java.time.LocalDateTime;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@Generated
public class BookingRequestDto {

    Long itemId;

    LocalDateTime start;

    LocalDateTime end;

    Status status;
}