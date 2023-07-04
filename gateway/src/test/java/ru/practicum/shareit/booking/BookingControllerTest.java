package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.header.HttpHeadersShareIt.X_SHARER_USER_ID;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {

	private final ObjectMapper mapper;
	private final MockMvc mockMvc;

	@MockBean
    private BookingClient bookingClient;

	private BookingRequestDto bookingRequestDto;

	@BeforeEach
	void setUp() {
		bookingRequestDto = BookingRequestDto
				.builder()
				.itemId(1L)
				.start(LocalDateTime.now().plusDays(1))
				.end(LocalDateTime.now().plusDays(2))
				.build();
	}

	@Test
	@SneakyThrows
	void createBookingWhenNoBookerHeaderThenMissingRequestHeaderExceptionTest() {
		mockMvc.perform(post("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(bookingRequestDto)))
				.andExpectAll(
						status().isBadRequest());
	}

	@Test
	@SneakyThrows
	void findBookersBookingsWhenStateIsIncorrectThenStatusIsBadRequestExceptionTest() {
		/*when(bookingService
				.findBookings(anyLong(), any(State.class), anyInt(), anyInt(), anyBoolean()))
				.thenThrow(new BadRequestException("Unknown state: " + "IncorrectState"));*/

		mockMvc.perform(get("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.param("state", "IncorrectState")
						.header(X_SHARER_USER_ID, "1"))
				.andExpectAll(
						status().isBadRequest());
	}

	@Test
	@SneakyThrows
	void findOwnersBookingsWhenStateIsInCorrectThenStatusIsBadRequestExceptionTest() {
		/*when(bookingService
				.findBookings(anyLong(), any(State.class), anyInt(), anyInt(), anyBoolean()))
				.thenThrow(new BadRequestException("Unknown state: " + "IncorrectState"));*/

		mockMvc.perform(get("/bookings/owner")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.param("state", "IncorrectState")
						.header(X_SHARER_USER_ID, "1"))
				.andExpectAll(
						status().isBadRequest());
	}

	@Test
	@SneakyThrows
	void testValidAnnotation() {

		mockMvc.perform(get("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.param("from","-1")
						.param("size", "1")
						.param("state", "ALL")
						.header(X_SHARER_USER_ID, "1"))
				.andExpectAll(
						status().isBadRequest());

		mockMvc.perform(get("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.param("from","0")
						.param("size", "0")
						.param("state", "ALL")
						.header(X_SHARER_USER_ID, "1"))
				.andExpectAll(
						status().isBadRequest());
	}

}
