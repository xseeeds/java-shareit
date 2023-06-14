package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemWithBookingResponseDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingShortResponseDto lastBooking;

    private BookingShortResponseDto nextBooking;

}