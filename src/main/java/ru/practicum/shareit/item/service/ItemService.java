package ru.practicum.shareit.item.service;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.expception.exp.BadRequestException;
import ru.practicum.shareit.expception.exp.NotFoundException;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsResponseDto;
import ru.practicum.shareit.item.dto.ItemWithBookingResponseDto;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.validation.Marker;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Service
@Validated
@Transactional(readOnly = true)
public interface ItemService {

    @Transactional
    @Modifying
    @Validated(Marker.OnCreate.class)
    ItemResponseDto createItem(@Positive long ownerId,
                               @Valid ItemResponseDto itemResponseDto);

    ItemResponseDto findItemDtoById(@Positive long itemId) throws NotFoundException;

    ItemWithBookingAndCommentsResponseDto findItemWithBookingAndCommentsResponseDtoById(@Positive long userId,
                                                                                        @Positive long itemId);

    @Transactional
    @Modifying
    @Validated(Marker.OnUpdate.class)
    ItemResponseDto updateItem(@Positive long ownerId,
                               @Positive long itemId,
                               @Valid ItemResponseDto itemResponseDto) throws NotFoundException;

    @Transactional
    @Modifying
    void deleteItemById(@Positive long ownerId,
                        @Positive long userId) throws NotFoundException;

    List<ItemResponseDto> findItemsResponseDtoByOwnerId(@Positive long ownerId,
                                                        @PositiveOrZero int from,
                                                        @Positive int size);

    List<ItemWithBookingResponseDto> findItemsWithBookingResponseDtoByOwnerId(@Positive long ownerId,
                                                                              @PositiveOrZero int from,
                                                                              @Positive int size);

    List<ItemResponseDto> findItemsIsNotRentedByNameOrDescription(@NotNull String text,
                                                                  @PositiveOrZero int from,
                                                                  @Positive int size);

    @Transactional
    @Modifying
    @Validated
    CommentResponseDto createComment(@Valid CommentResponseDto commentResponseDto,
                                     @Positive long itemId,
                                     @Positive long bookerId) throws BadRequestException;


    ItemEntity findItemEntityById(@Positive long itemId) throws NotFoundException;

    void checkItemIsExistById(@Positive long itemId);

}
