package ru.practicum.shareit.booking.dto;


import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.validation.annotation.ValidStartEndFields;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime start;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime end;

    Status status;
}