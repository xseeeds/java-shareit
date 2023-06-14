package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsResponseDto;
import ru.practicum.shareit.item.dto.ItemWithBookingResponseDto;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.item.repository.ItemWithBookingProjection;

import java.util.List;

@UtilityClass
public class ItemMapper {

    public ItemResponseDto toItemDto(ItemEntity itemEntity) {
        return ItemResponseDto
                .builder()
                .id(itemEntity.getId())
                .name(itemEntity.getName())
                .description(itemEntity.getDescription())
                .available(itemEntity.getAvailable())
                .build();
    }

    public ItemEntity toItem(ItemResponseDto itemResponseDto, long ownerId) {
        return ItemEntity
                .builder()
                .name(itemResponseDto.getName())
                .description(itemResponseDto.getDescription())
                .available(itemResponseDto.getAvailable())
                .ownerId(ownerId)
                .build();
    }

    public ItemWithBookingResponseDto toItemWithBookingResponseDto(ItemWithBookingProjection projection) {
        BookingShortResponseDto lastBooking = null;
        BookingShortResponseDto nextBooking = null;

        if (projection.getLastBookingEntityId() != null) {
            lastBooking = new BookingShortResponseDto(
                    projection.getLastBookingEntityId(),
                    projection.getLastBookingEntityBookerId(),
                    projection.getLastBookingEntityStart(),
                    projection.getLastBookingEntityEnd());
        }
        if (projection.getNextBookingEntityId() != null) {
            nextBooking = new BookingShortResponseDto(
                    projection.getNextBookingEntityId(),
                    projection.getNextBookingEntityBookerId(),
                    projection.getNextBookingEntityStart(),
                    projection.getNextBookingEntityEnd());
        }

        return ItemWithBookingResponseDto
                .builder()
                .id(projection.getId())
                .name(projection.getName())
                .description(projection.getDescription())
                .available(projection.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();
    }

    public ItemWithBookingAndCommentsResponseDto toItemWithCommentsResponseDto(ItemEntity itemEntity,
                                                                               List<CommentResponseDto> commentResponseDtoList) {
        return ItemWithBookingAndCommentsResponseDto
                .builder()
                .id(itemEntity.getId())
                .name(itemEntity.getName())
                .description(itemEntity.getDescription())
                .available(itemEntity.getAvailable())
                .comments(commentResponseDtoList)
                .build();
    }
}
