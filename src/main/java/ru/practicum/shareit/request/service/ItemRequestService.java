package ru.practicum.shareit.request.service;

import ru.practicum.shareit.expception.exp.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

public interface ItemRequestService {

    ItemRequestResponseDto createItemRequest(@Positive long requesterId,
                                             @Valid ItemRequestDto itemRequestDto) throws NotFoundException;

    ItemRequestResponseDto findItemRequestById(@Positive long itemRequestId,
                                               @Positive long userId) throws NotFoundException;

    List<ItemRequestResponseDto> findOwnerItemRequest(@Positive long requesterId,
                                                      @PositiveOrZero int from,
                                                      @Positive int size) throws NotFoundException;

    List<ItemRequestResponseDto> findOthersItemRequest(@Positive long userId,
                                                       @PositiveOrZero int from,
                                                       @Positive int size) throws NotFoundException;

}
