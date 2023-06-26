package ru.practicum.shareit.booking.dto;


import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.validation.annotation.ValidStartEndFields;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.shareit.util.Util.DATE_TIME_FORMAT_PATTERN;


@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@ValidStartEndFields
@Generated
public class BookingRequestDto {

    Long id;

    @NotNull
    Long itemId;

    @NotNull
    @DateTimeFormat(pattern = DATE_TIME_FORMAT_PATTERN)
    LocalDateTime start;

    @NotNull
    @DateTimeFormat(pattern = DATE_TIME_FORMAT_PATTERN)
    LocalDateTime end;

    Status status;
}