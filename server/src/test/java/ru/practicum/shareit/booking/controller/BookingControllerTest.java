package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exception.exp.BadRequestException;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;
import ru.practicum.shareit.user.dto.UserIdShortResponseDto;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.header.HttpHeadersShareIt.X_SHARER_USER_ID;

@WebMvcTest(controllers = {BookingController.class})
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {

    private final ObjectMapper mapper;
    private final MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;
    private BookingRequestDto bookingRequestDto;
    private BookingResponseDto bookingResponseDto;

    @BeforeEach
    void setUp() {
        bookingRequestDto = BookingRequestDto
                .builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        final ItemShortResponseDto itemShortResponseDto = ItemShortResponseDto
                .builder()
                .id(1L)
                .name("Item")
                .build();

        final UserIdShortResponseDto userIdShortResponseDto = UserIdShortResponseDto
                .builder()
                .id(1L)
                .build();

        bookingResponseDto = BookingResponseDto
                .builder()
                .item(itemShortResponseDto)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Status.WAITING)
                .booker(userIdShortResponseDto)
                .build();
    }

    @Test
    @SneakyThrows
    void createBookingWhenItemIsAvailableThenReturnBookingTest() {
        when(bookingService
                .createBooking(1, bookingRequestDto))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, "1")
                        .content(mapper.writeValueAsString(bookingRequestDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.status", equalTo("WAITING")),
                        jsonPath("$.item.name", equalTo("Item")));

    }

    @Test
    @SneakyThrows
    void createBookingWhenItemIsNotAvailableThenBadRequestExceptionTest() {
        when(bookingService
                .createBooking(anyLong(), any(BookingRequestDto.class)))
                .thenThrow(new BadRequestException("Вещь id => " + 1L + " не доступна для бронирования"));


        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, "1")
                        .content(mapper.writeValueAsString(bookingRequestDto)))
                .andExpectAll(
                        status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void createBookingWhenBookerIsOwnerThenBadRequestExceptionTest() {
        when(bookingService
                .createBooking(anyLong(), any(BookingRequestDto.class)))
                .thenThrow(new BadRequestException("Пользователь id => " + 1L
                        + " не может забронировать свою вещь id => " + 1L));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, "1")
                        .content(mapper.writeValueAsString(bookingRequestDto)))
                .andExpectAll(
                        status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void postBookingWhenNoBookerHeaderThenMissingRequestHeaderExceptionTest() {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingRequestDto)))
                .andExpectAll(
                        status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void approveBookingWhenStatusIsWaitingThenBookingApprovedTest() {
        final BookingResponseDto bookingApproved = bookingResponseDto
                .toBuilder()
                .status(Status.APPROVED)
                .build();

        when(bookingService
                .approveBookingById(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingApproved);

        mockMvc.perform(patch("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, "1")
                        .param("approved", "true"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.status", equalTo("APPROVED")),
                        jsonPath("$.item.name", equalTo("Item")));
    }

    @Test
    @SneakyThrows
    void approveBookingWhenStatusIsNotWaitingThenBadRequestExceptionTest() {
        when(bookingService
                .approveBookingById(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new BadRequestException("Статус бронирования id => " + 1L + " не WAITING!"));

        mockMvc.perform(patch("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, "1")
                        .param("approved", "true"))
                .andExpectAll(
                        status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void findBookingByIdWhenBookingFoundThenBookingReturnTest() {
        when(bookingService
                .findBookingById(anyLong(), anyLong()))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, "1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.status", equalTo("WAITING")),
                        jsonPath("$.item.name", equalTo("Item")));
    }

    @Test
    @SneakyThrows
    void findBookersBookingsWhenStateIsCorrectThenStatusIsOkTest() {
        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("state", "ALL")
                        .header(X_SHARER_USER_ID, "1"))
                .andExpectAll(
                        status().isOk());
    }

    @Test
    @SneakyThrows
    void findOwnersBookingsWhenStateIsCorrectThenStatusIsOkTest() {
        mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("state", "ALL")
                        .header(X_SHARER_USER_ID, "1"))
                .andExpectAll(
                        status().isOk());
    }

}