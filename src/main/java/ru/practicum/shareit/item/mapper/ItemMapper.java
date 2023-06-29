package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsResponseDto;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.item.repository.ItemShortResponseDtoProjection;
import ru.practicum.shareit.request.dto.ItemRequestShortResponseDto;


import java.util.List;


@UtilityClass
public class ItemMapper {

    public ItemResponseDto toItemResponseDto(ItemEntity itemEntity) {
        return ItemResponseDto
                .builder()
                .id(itemEntity.getId())
                .name(itemEntity.getName())
                .description(itemEntity.getDescription())
                .available(itemEntity.getAvailable())
                .requestId(itemEntity.getRequestId())
                .build();
    }

    public ItemEntity toItemEntity(ItemResponseDto itemResponseDto, long ownerId) {
        return ItemEntity
                .builder()
                .name(itemResponseDto.getName())
                .description(itemResponseDto.getDescription())
                .available(itemResponseDto.getAvailable())
                .requestId(itemResponseDto.getRequestId())
                .ownerId(ownerId)
                .build();
    }

    public ItemWithBookingAndCommentsResponseDto toItemWithBookingAndCommentsResponseDto(ItemEntity itemEntity,
                                                                                         BookingShortResponseDto lastBooking,
                                                                                         BookingShortResponseDto nextBooking,
                                                                                         List<CommentResponseDto> commentResponseDtoListByItemIdOrderByCreatedDesc) {
        return ItemWithBookingAndCommentsResponseDto
                .builder()
                .id(itemEntity.getId())
                .name(itemEntity.getName())
                .description(itemEntity.getDescription())
                .available(itemEntity.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .request(ItemRequestShortResponseDto
                        .builder()
                        .id(itemEntity.getRequestId())
                        .description(itemEntity.getRequest() != null ? itemEntity.getRequest().getDescription() : null)
                        .created(itemEntity.getRequest() != null ? itemEntity.getRequest().getCreated() : null)
                        .requesterId(itemEntity.getRequest() != null ? itemEntity.getRequest().getRequesterId() : null)
                        .build())
                .comments(commentResponseDtoListByItemIdOrderByCreatedDesc)
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
                .request(ItemRequestShortResponseDto
                        .builder()
                        .id(itemEntity.getRequestId())
                        .description(itemEntity.getRequest() != null ? itemEntity.getRequest().getDescription() : null)
                        .created(itemEntity.getRequest() != null ? itemEntity.getRequest().getCreated() : null)
                        .requesterId(itemEntity.getRequest() != null ? itemEntity.getRequest().getRequesterId() : null)
                        .build())
                .comments(commentResponseDtoList)
                .build();
    }

    public ItemShortResponseDto toItemShortResponseDto(ItemShortResponseDtoProjection itemShortResponseDtoProjection) {
        return ItemShortResponseDto
                .builder()
                .id(itemShortResponseDtoProjection.getId())
                .name(itemShortResponseDtoProjection.getName())
                .description(itemShortResponseDtoProjection.getDescription())
                .available(itemShortResponseDtoProjection.getAvailable())
                .requestId(itemShortResponseDtoProjection.getRequestId())
                .build();
    }
}
