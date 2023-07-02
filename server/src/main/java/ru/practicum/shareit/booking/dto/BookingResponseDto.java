package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;
import ru.practicum.shareit.user.dto.UserIdShortResponseDto;

import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Generated
public class BookingResponseDto {

    Long id;

    ItemShortResponseDto item;

    LocalDateTime start;

    LocalDateTime end;

    UserIdShortResponseDto booker;

    Status status;

}
