package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.header.HttpHeadersShareIt.X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(@RequestHeader(X_SHARER_USER_ID) long bookerId,
                                            @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("SERVER => createBooking bookerId => {}", bookerId);
        return bookingService.createBooking(bookerId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBookingById(@RequestHeader(X_SHARER_USER_ID) long ownerId,
                                                 @PathVariable long bookingId,
                                                 @RequestParam boolean approved) {
        log.info("SERVER => approveBookingById ownerId => {}, bookingId => {}, approved => {}", ownerId, bookingId, approved);
        return bookingService.approveBookingById(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto findBookingById(@RequestHeader(X_SHARER_USER_ID) long ownerId,
                                              @PathVariable long bookingId) {
        log.info("SERVER => findBookingById ownerId => {}, bookingId => {}", ownerId, bookingId);
        return bookingService.findBookingById(ownerId, bookingId);
    }

    @GetMapping()
    public List<BookingResponseDto> findBookersBookings(@RequestHeader(X_SHARER_USER_ID) long bookerId,
                                                        @RequestParam(value = "state", defaultValue = "ALL") State state,
                                                        @RequestParam(value = "from", defaultValue = "0") int from,
                                                        @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("SERVER => findBookersBookings bookerId => {}, state => {}", bookerId, state);
        return bookingService.findBookings(bookerId, state, from, size, true);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> findOwnersBookings(@RequestHeader(X_SHARER_USER_ID) long ownerId,
                                                       @RequestParam(value = "state", defaultValue = "ALL") State state,
                                                       @RequestParam(value = "from", defaultValue = "0") int from,
                                                       @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("SERVER => findOwnersBookings ownerId => {}, state => {}", ownerId, state);
        return bookingService.findBookings(ownerId, state, from, size, false);
    }

}