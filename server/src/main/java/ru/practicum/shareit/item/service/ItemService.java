package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.exp.BadRequestException;
import ru.practicum.shareit.exception.exp.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.ItemEntity;

import java.util.List;

public interface ItemService {

    ItemResponseDto createItem(long ownerId,
                               ItemRequestDto itemRequestDto) throws NotFoundException;

    ItemWithBookingAndCommentsResponseDto findItemWithBookingAndCommentsResponseDtoById(long userId,
                                                                                        long itemId,
                                                                                        int from,
                                                                                        int size) throws NotFoundException;

    ItemResponseDto updateItem(long ownerId,
                               long itemId,
                               ItemRequestDto itemRequestDto) throws NotFoundException;

    void deleteItemById(long ownerId,
                        long userId) throws NotFoundException;

    List<ItemWithBookingAndCommentsResponseDto> findItemWithBookingAndCommentsResponseDtoByOwnerId(long ownerId,
                                                                                                   int from,
                                                                                                   int size) throws NotFoundException;

    List<ItemResponseDto> findItemIsAvailableByNameOrDescription(String text,
                                                                 int from,
                                                                 int size);

    CommentResponseDto createComment(long bookerId,
                                     long itemId,
                                     CommentRequestDto commentRequestDto) throws BadRequestException, NotFoundException;

    List<ItemShortResponseDto> findItemShortResponseDtoByRequestIdIn(List<Long> requestIds);

    ItemEntity findItemEntityById(long itemId) throws NotFoundException;

    void checkItemIsExistById(long itemId) throws NotFoundException;

}
