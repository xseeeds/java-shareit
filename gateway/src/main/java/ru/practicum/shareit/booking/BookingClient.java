package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.client.BaseClient;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> postBooking(long bookerId,BookingRequestDto bookingRequestDto) {
        return post("", bookerId, bookingRequestDto);
    }

    public ResponseEntity<Object> approveBooking(long ownerId, long bookingId, boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved, ownerId, ownerId);
    }

    public ResponseEntity<Object> getBookingById(long userId, long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookersBookings(long bookerId, State state, int from, int size) {
        final String path = "?state=" + state + "&from=" + from + "&size=" + size;
        return get(path, bookerId);
    }

    public ResponseEntity<Object> getOwnersBookings(long ownerId, State state, int from, int size) {
        final String path = "/owner?state=" + state + "&from=" + from + "&size=" + size;
        return get(path, ownerId);
    }

}
