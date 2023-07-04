package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Generated
public class BookingShortResponseDto {

	Long id;

	Long bookerId;

	LocalDateTime start;

	LocalDateTime end;

}
