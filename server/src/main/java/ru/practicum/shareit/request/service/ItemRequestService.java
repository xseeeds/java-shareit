package ru.practicum.shareit.request.service;

import ru.practicum.shareit.exception.exp.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestResponseDto createItemRequest(long requesterId,
                                             ItemRequestRequestDto itemRequestDto) throws NotFoundException;

    ItemRequestResponseDto findItemRequestById(long itemRequestId,
                                               long userId) throws NotFoundException;

    List<ItemRequestResponseDto> findOwnerItemRequest(long requesterId,
                                                      int from,
                                                      int size) throws NotFoundException;

    List<ItemRequestResponseDto> findOthersItemRequest(long userId,
                                                       int from,
                                                       int size) throws NotFoundException;

}
