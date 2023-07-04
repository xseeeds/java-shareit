package ru.practicum.shareit.booking.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.validation.ValidStartEndFields;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.shareit.validation.Converter.DATE_TIME_FORMAT_PATTERN;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@ValidStartEndFields
@Generated
public class BookingRequestDto {

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