package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import ru.practicum.shareit.expception.exp.BadRequestException;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {

	private final BookingService bookingService;
	private final UserService userService;
	private final ItemService itemService;

	@Test
	void createBookingWhenItemIsNotAvailableThenNotFoundExceptionTest() {
		final long ownerId = userService
				.createUser(UserResponseDto
						.builder()
						.name("Owner")
						.email("owner@ya.ru")
						.build())
				.getId();
		final ItemResponseDto itemResponseDto = ItemResponseDto
				.builder()
				.name("Item")
				.description("Item")
				.available(false)
				.build();
		final long itemId = itemService
				.createItem(ownerId, itemResponseDto)
				.getId();
		final BookingRequestDto bookingRequestDto = BookingRequestDto
				.builder()
				.itemId(itemId)
				.start(LocalDateTime.now().plusDays(1))
				.end(LocalDateTime.now().plusDays(2))
				.build();

		assertThrows(BadRequestException.class,
				() -> bookingService.createBooking(ownerId, bookingRequestDto));

	}

	@Test
	void testValidAnnotation() {
		assertThrows(ConstraintViolationException.class,
				() -> bookingService.findBookings(1L, "ALL", -1, 1, true));
		assertThrows(ConstraintViolationException.class,
				() -> bookingService.findBookings(1L, "ALL", 0, 0, true));
		assertThrows(ConstraintViolationException.class,
				() -> bookingService.findBookings(1L, null, 0, 1, true));

	}

}
