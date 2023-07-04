package ru.practicum.shareit.item.dto.item;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestShortResponseDto;

import java.util.List;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Generated
public class ItemWithBookingAndCommentsResponseDto {

	Long id;

	String name;

	String description;

	Boolean available;

	BookingShortResponseDto lastBooking;

	BookingShortResponseDto nextBooking;

	List<CommentResponseDto> comments;

	ItemRequestShortResponseDto request;

}