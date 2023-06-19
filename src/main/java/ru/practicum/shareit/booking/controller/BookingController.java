package ru.practicum.shareit.booking.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.header.HttpHeadersShareIt;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(@RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long bookerId,
                                            @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingService.createBooking(bookingRequestDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBookingById(@RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long ownerId,
                                                 @PathVariable long bookingId,
                                                 @RequestParam boolean approved) {
        return bookingService.approveBookingById(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto findBookingById(@RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long ownerId,
                                              @PathVariable long bookingId) {
        return bookingService.findBookingById(ownerId, bookingId);
    }

    @GetMapping()
    public List<BookingResponseDto> findBookersBookings(@RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long bookerId,
                                                        @RequestParam(required = false, defaultValue = "ALL") String state,
                                                        @RequestParam(value = "from", defaultValue = "0") int from,
                                                        @RequestParam(value = "size", defaultValue = "10") int size) {
        return bookingService.findBookings(bookerId, state, from, size, true);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> findOwnersBookings(@RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long ownerId,
                                                       @RequestParam(required = false, defaultValue = "ALL") String state,
                                                       @RequestParam(value = "from", defaultValue = "0") int from,
                                                       @RequestParam(value = "size", defaultValue = "10") int size) {
        return bookingService.findBookings(ownerId, state, from, size, false);
    }

}