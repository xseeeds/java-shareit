package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.expception.exp.NotFoundException;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequestEntity;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Util;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Modifying
    @Transactional
    @Validated
    @Override
    public ItemRequestResponseDto createItemRequest(@Positive long requesterId,
                                                    @Valid ItemRequestDto itemRequestDto) throws NotFoundException {
        userService.checkUserIsExistById(requesterId);
        final ItemRequestResponseDto itemRequestResponseDto = ItemRequestMapper
                .toItemRequestResponseDto(
                        requestRepository.save(
                                ItemRequestMapper.toItemRequestEntityAndCreatedNow(itemRequestDto, requesterId)), Collections.emptyList());
        log.info("Пользователем id => {} создан запрос id => {}", requesterId, itemRequestResponseDto.getId());
        return itemRequestResponseDto;
    }

    @Override
    public ItemRequestResponseDto findItemRequestById(@Positive long itemRequestId,
                                                      @Positive long userId) throws NotFoundException {
        userService.checkUserIsExistById(userId);

        final ItemRequestEntity itemRequestEntity = requestRepository
                .findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException("Запрос id => " + itemRequestId + " не найден"));

        final List<ItemShortResponseDto> itemShortResponseDtoList = itemService
                .findItemShortResponseDtoByRequestIdIn(List.of(itemRequestId));

        final ItemRequestResponseDto itemRequestResponseDto = ItemRequestMapper
                .toItemRequestResponseDto(itemRequestEntity, itemShortResponseDtoList);

        log.info("Пользователем id => {} осуществлен поиск запроса id => {}", userId, itemRequestId);
        return itemRequestResponseDto;
    }

    @Override
    public List<ItemRequestResponseDto> findOwnerItemRequest(@Positive long requesterId,
                                                             @PositiveOrZero int from,
                                                             @Positive int size) throws NotFoundException {
        userService.checkUserIsExistById(requesterId);
        final Page<ItemRequestEntity> itemRequestsEntityPage = requestRepository
                .findAllByRequesterIdOrderByCreatedDesc(requesterId, Util.getPageSortAscByProperties(from, size, "id"));
        final List<ItemRequestResponseDto> itemRequestResponseDtoList = this
                .findItemShortListByItemRequestIds(itemRequestsEntityPage.getContent());
        log.info("Пользователь id => {} получил запросы вещей size => {} получены",
                requesterId, itemRequestResponseDtoList.size());
        return itemRequestResponseDtoList;
    }

    @Override
    public List<ItemRequestResponseDto> findOthersItemRequest(@Positive long userId,
                                                              @PositiveOrZero int from,
                                                              @Positive int size) throws NotFoundException {
        userService.checkUserIsExistById(userId);
        final Page<ItemRequestEntity> itemRequestsEntityPage = requestRepository
                .findAllByRequesterIdIsNotOrderByCreatedDesc(userId, Util.getPageSortAscByProperties(from, size, "id"));
        final List<ItemRequestResponseDto> itemRequestResponseDtoList = this
                .findItemShortListByItemRequestIds(itemRequestsEntityPage.getContent());
        log.info("Пользователь id => {} получил запросы вещей size => {} получены",
                userId, itemRequestResponseDtoList.size());
        return itemRequestResponseDtoList;
    }

    private List<ItemRequestResponseDto> findItemShortListByItemRequestIds(List<ItemRequestEntity> itemRequestEntityList) {
        final List<ItemShortResponseDto> itemShortResponseDtoList = itemService
                .findItemShortResponseDtoByRequestIdIn(
                        itemRequestEntityList
                                .stream()
                                .map(ItemRequestEntity::getId)
                                .collect(toList()));

        final Map<Long, List<ItemShortResponseDto>> itemShortMap = itemShortResponseDtoList
                .stream()
                .collect(groupingBy(
                        ItemShortResponseDto::getRequestId, toList()));

        final List<ItemRequestResponseDto> itemRequestResponseDtoList = itemRequestEntityList
                .stream()
                .map(itemRequest ->
                        ItemRequestMapper
                                .toItemRequestResponseDto(itemRequest, itemShortMap
                                        .getOrDefault(itemRequest.getId(), Collections.emptyList())))
                .collect(toList());

        log.info("Запросы вещей size => {} получены для СЕРВИСОВ", itemRequestResponseDtoList.size());
        return itemRequestResponseDtoList;
    }
}