package ru.practicum.shareit.request.service;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.expception.exp.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Service
@Validated
@Transactional(readOnly = true)
public interface ItemRequestService {

    @Modifying
    @Transactional
    @Validated
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
