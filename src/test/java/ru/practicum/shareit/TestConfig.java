package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.expception.controller.ErrorHandler;

import static org.mockito.Mockito.mock;

@Configuration
public class TestConfig {

/*

    @Bean
    public ErrorHandler errorHandler() {
        return new ErrorHandler();
    }

    @Bean
    public BookingService bookingService() {
        return mock(BookingService.class);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
*/

}
