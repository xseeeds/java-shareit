package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsResponseDto;
import ru.practicum.shareit.item.model.CommentEntity;
import ru.practicum.shareit.item.model.ItemEntity;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@UtilityClass
public class ItemMapper {

    public ItemResponseDto toItemResponseDto(ItemEntity itemEntity) {
        return ItemResponseDto
                .builder()
                .id(itemEntity.getId())
                .name(itemEntity.getName())
                .description(itemEntity.getDescription())
                .available(itemEntity.getAvailable())
                .build();
    }

    public ItemEntity toItemEntity(ItemResponseDto itemResponseDto, long ownerId) {
        return ItemEntity
                .builder()
                .name(itemResponseDto.getName())
                .description(itemResponseDto.getDescription())
                .available(itemResponseDto.getAvailable())
                .ownerId(ownerId)
                .build();
    }

    public ItemWithBookingAndCommentsResponseDto toItemWithBookingAndCommentsResponseDto(ItemEntity itemEntity,
                                                                                         BookingShortResponseDto lastBooking,
                                                                                         BookingShortResponseDto nextBooking,
                                                                                         List<CommentResponseDto> commentEntityListByItemIdOrderByCreatedDesc) {
        return ItemWithBookingAndCommentsResponseDto
                .builder()
                .id(itemEntity.getId())
                .name(itemEntity.getName())
                .description(itemEntity.getDescription())
                .available(itemEntity.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(commentEntityListByItemIdOrderByCreatedDesc)
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
