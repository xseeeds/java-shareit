package ru.practicum.shareit.item.service;

import ru.practicum.shareit.expception.exp.BadRequestException;
import ru.practicum.shareit.expception.exp.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.ItemEntity;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


public interface ItemService {

    ItemResponseDto createItem(@Positive long ownerId,
                               @Valid ItemResponseDto itemResponseDto) throws NotFoundException;

    ItemWithBookingAndCommentsResponseDto findItemWithBookingAndCommentsResponseDtoById(@Positive long userId,
                                                                                        @Positive long itemId,
                                                                                        @PositiveOrZero int from,
                                                                                        @Positive int size) throws NotFoundException;


    ItemResponseDto updateItem(@Positive long ownerId,
                               @Positive long itemId,
                               @Valid ItemResponseDto itemResponseDto) throws NotFoundException;

    void deleteItemById(@Positive long ownerId,
                        @Positive long userId) throws NotFoundException;

    List<ItemWithBookingAndCommentsResponseDto> findItemWithBookingAndCommentsResponseDtoByOwnerId(@Positive long ownerId,
                                                                                                   @PositiveOrZero int from,
                                                                                                   @Positive int size) throws NotFoundException;

    List<ItemResponseDto> findItemIsAvailableByNameOrDescription(@NotNull String text,
                                                                 @PositiveOrZero int from,
                                                                 @Positive int size);

    CommentResponseDto createComment(@Positive long bookerId,
                                     @Positive long itemId,
                                     @Valid CommentRequestDto commentRequestDto) throws BadRequestException, NotFoundException;

    List<ItemShortResponseDto> findItemShortResponseDtoByRequestIdIn(@NotNull List<Long> requestIds);

    ItemEntity findItemEntityById(@Positive long itemId) throws NotFoundException;

    void checkItemIsExistById(@Positive long itemId) throws NotFoundException;

}
