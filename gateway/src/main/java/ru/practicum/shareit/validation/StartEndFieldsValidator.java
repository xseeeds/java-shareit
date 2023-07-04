package ru.practicum.shareit.validation;

import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class StartEndFieldsValidator implements ConstraintValidator<ValidStartEndFields, BookingRequestDto> {

	@Override
	public void initialize(ValidStartEndFields validStartEndFields) {
	}

	@Override
	public boolean isValid(BookingRequestDto bookingRequestDto, ConstraintValidatorContext constraintValidatorContext) {
		final LocalDateTime current = LocalDateTime.now();
		final LocalDateTime start = bookingRequestDto.getStart();
		final LocalDateTime end = bookingRequestDto.getEnd();

		if (start == null || end == null) {
			return false;
		}

		return start.isAfter(current) && end.isAfter(start);
	}

}