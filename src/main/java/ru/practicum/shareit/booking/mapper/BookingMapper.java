package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.booking.repository.BookingShortDtoProjection;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.user.dto.UserIdShortResponseDto;

@UtilityClass
public class BookingMapper {

    public BookingResponseDto toBookingResponseDto(BookingEntity bookingEntity) {
        return BookingResponseDto.builder()
                .id(bookingEntity.getId())
                .item(ItemShortResponseDto
                        .builder()
                        .id(bookingEntity.getItemId())
                        .name(bookingEntity.getItem() != null ? bookingEntity.getItem().getName() : null)
                        .description(bookingEntity.getItem() != null ? bookingEntity.getItem().getDescription() : null)
                        .available(bookingEntity.getItem() != null ? bookingEntity.getItem().getAvailable() : null)
                        .requestId(bookingEntity.getItem() != null ? bookingEntity.getItem().getRequestId() : null)
                        .build())
                .start(bookingEntity.getStart())
                .end(bookingEntity.getEnd())
                .booker(UserIdShortResponseDto
                        .builder()
                        .id(bookingEntity.getBookerId())
                        .build())
                .status(bookingEntity.getStatus())
                .build();
    }

    public BookingEntity toBookingAndStatusWaiting(BookingRequestDto bookingRequestDto, ItemEntity itemEntity, long bookerId) {
        return BookingEntity.builder()
                .id(bookingRequestDto.getId())
                .item(itemEntity)
                .itemId(itemEntity.getId())
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .bookerId(bookerId)
                .status(Status.WAITING)
                .build();
    }

    public BookingShortResponseDto toBookingShortResponseDto(BookingShortDtoProjection projection) {
        return BookingShortResponseDto
                .builder()
                .id(projection.getId())
                .bookerId(projection.getBookerId())
                .start(projection.getStart())
                .end(projection.getEnd())
                .build();
    }

    public BookingShortResponseDto toBookingShortResponseDto(BookingEntity bookingEntity) {
        return BookingShortResponseDto
                .builder()
                .id(bookingEntity.getId())
                .bookerId(bookingEntity.getBookerId())
                .start(bookingEntity.getStart())
                .end(bookingEntity.getEnd())
                .build();
    }

}
