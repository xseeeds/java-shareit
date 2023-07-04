package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.exception.exp.UnknownBookingStateException;
import ru.practicum.shareit.validation.Converter;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.header.HttpHeadersShareIt.X_SHARER_USER_ID;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@Positive @RequestHeader(X_SHARER_USER_ID) long bookerId,
                                                @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("GATEWAY => createBooking bookerId => {}", bookerId);
        return bookingClient.postBooking(bookerId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBookingById(@Positive @RequestHeader(X_SHARER_USER_ID) long ownerId,
                                                     @Positive @PathVariable long bookingId,
                                                     @RequestParam boolean approved) {
        log.info("GATEWAY => approveBookingById ownerId => {}, bookingId => {}, approved => {}", ownerId, bookingId, approved);
        return bookingClient.approveBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findBookingById(@Positive @RequestHeader(X_SHARER_USER_ID) long ownerId,
                                                  @Positive @PathVariable long bookingId) {
        log.info("GATEWAY => findBookingById ownerId => {}, bookingId => {}", ownerId, bookingId);
        return bookingClient.getBookingById(ownerId, bookingId);
    }

    @GetMapping()
    public ResponseEntity<Object> findBookersBookings(@Positive @RequestHeader(X_SHARER_USER_ID) long bookerId,
                                                      @RequestParam(value = "state", defaultValue = "ALL") String s,
                                                      @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
                                                      @Positive @RequestParam(value = "size", defaultValue = "10") int size) throws UnknownBookingStateException {
        final State state = Converter.getState(s);
        log.info("GATEWAY => findBookersBookings bookerId => {}, state => {}", bookerId, state);
        return bookingClient.getBookersBookings(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findOwnersBookings(@Positive @RequestHeader(X_SHARER_USER_ID) long ownerId,
                                                     @RequestParam(value = "state", defaultValue = "ALL") String s,
                                                     @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
                                                     @Positive @RequestParam(value = "size", defaultValue = "10") int size) throws UnknownBookingStateException {
        final State state = Converter.getState(s);
        log.info("GATEWAY => findOwnersBookings ownerId => {}, state => {}", ownerId, state);
        return bookingClient.getOwnersBookings(ownerId, state, from, size);
    }

}

