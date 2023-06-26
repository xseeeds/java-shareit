package ru.practicum.shareit.booking.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.header.HttpHeadersShareIt;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(@RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long bookerId,
                                            @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("createBooking bookerId => {}", bookerId);
        return bookingService.createBooking(bookerId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBookingById(@RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long ownerId,
                                                 @PathVariable long bookingId,
                                                 @RequestParam boolean approved) {
        log.info("approveBookingById ownerId => {}, bookingId => {}", ownerId, bookingId);
        return bookingService.approveBookingById(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto findBookingById(@RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long ownerId,
                                              @PathVariable long bookingId) {
        log.info("findBookingById ownerId => {}, bookingId => {}", ownerId, bookingId);
        return bookingService.findBookingById(ownerId, bookingId);
    }

    @GetMapping()
    public List<BookingResponseDto> findBookersBookings(@RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long bookerId,
                                                        @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                        @RequestParam(value = "from", defaultValue = "0") int from,
                                                        @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("findBookersBookings bookerId => {}, stata => {}", bookerId, state);
        return bookingService.findBookings(bookerId, state, from, size, true);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> findOwnersBookings(@RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long ownerId,
                                                       @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                       @RequestParam(value = "from", defaultValue = "0") int from,
                                                       @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("findOwnersBookings ownerId => {}, stata => {}", ownerId, state);
        return bookingService.findBookings(ownerId, state, from, size, false);
    }

}