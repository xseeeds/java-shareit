package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.expception.exp.BadRequestException;
import ru.practicum.shareit.expception.exp.NotFoundException;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsResponseDto;
import ru.practicum.shareit.item.dto.ItemWithBookingResponseDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserNameProjection;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.Marker;
import ru.practicum.shareit.util.Util;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final BookingService bookingService;


    @Transactional
    @Validated(Marker.OnCreate.class)
    @Modifying
    @Override
    public ItemResponseDto createItem(@Positive long ownerId,
                                      @Valid ItemResponseDto itemResponseDto) {
        userService.checkUserIsExistById(ownerId);
        final ItemResponseDto savedItem = ItemMapper.toItemDto(
                itemRepository.save(
                        ItemMapper.toItem(itemResponseDto, ownerId)
                )
        );
        log.info("Пользователем с id => {} создана вещь с id => {}", ownerId, savedItem.getId());
        return savedItem;

    }

    @Override
    public ItemResponseDto findItemDtoById(@Positive long itemId) throws NotFoundException {
        final ItemResponseDto foundItem = ItemMapper.toItemDto(
                itemRepository.findById(itemId)
                        .orElseThrow(
                                () -> new NotFoundException("Вещь по id => " + itemId + " не существует")
                        )
        );
        log.info("Вещь по id => {} получена", itemId);
        return foundItem;
    }

    @Override
    public ItemWithBookingAndCommentsResponseDto findItemWithBookingAndCommentsResponseDtoById(@Positive long userId,
                                                                                               @Positive long itemId,
                                                                                               @PositiveOrZero int from,
                                                                                               @Positive int size) {
        final ItemEntity itemEntity = findItemEntityById(itemId);

        final List<CommentResponseDto> commentResponseDtoList = commentRepository
                .findAllByItemIdOrderByCreatedDesc(itemId)
                .stream()
                .map(commentEntity ->  CommentMapper.toCommentResponseDto(
                                commentEntity, commentEntity.getAuthor().getName()))
                .collect(toList());

        final ItemWithBookingAndCommentsResponseDto itemWithBookingAndCommentsResponseDto = ItemMapper
                .toItemWithCommentsResponseDto(itemEntity, commentResponseDtoList);

        if (itemEntity.getOwner().getId().equals(userId)) {
            final LocalDateTime now = LocalDateTime.now();
            bookingService
                    .findLastAndNextBookingEntity(itemEntity.getId(), now)
                    .forEach(bookingEntity -> {
                        if (bookingEntity.getStart().isBefore(now)) {
                            itemWithBookingAndCommentsResponseDto.setLastBooking(BookingMapper.toBookingShortResponseDto(bookingEntity));
                        } else if (bookingEntity.getStart().isAfter(now)) {
                            itemWithBookingAndCommentsResponseDto.setNextBooking(BookingMapper.toBookingShortResponseDto(bookingEntity));
                        }
                    });
            log.info("Владелец по id => {}", userId);
        }
        log.info("Пользователь по id => {} выполнил запрос на вещь с бронированием и комментариями по id => {} получена", userId, itemId);
        return itemWithBookingAndCommentsResponseDto;
    }

    @Transactional
    @Validated(Marker.OnUpdate.class)
    @Modifying
    @Override
    public ItemResponseDto updateItem(@Positive long ownerId,
                                      @Positive long itemId,
                                      @Valid ItemResponseDto itemResponseDto) throws NotFoundException {
        ItemEntity itemEntity = itemRepository
                .findByIdAndOwnerId(itemId, ownerId)
                .orElseThrow(
                        () -> new NotFoundException("Вещь по id => " + itemId + " не принадлежит пользователю с id => " + ownerId)
                );
        if (itemResponseDto.getName() != null) {
            itemEntity.setName(itemResponseDto.getName());
        }
        if (itemResponseDto.getDescription() != null) {
            itemEntity.setDescription(itemResponseDto.getDescription());
        }
        if (itemResponseDto.getAvailable() != null) {
            itemEntity.setAvailable(itemResponseDto.getAvailable());
        }
        final ItemResponseDto savedItem = ItemMapper.toItemDto(itemEntity);
        log.info("Пользователем с id => {} обновлена вещь с id => {}", ownerId, itemId);
        return savedItem;
    }

    @Transactional
    @Modifying
    @Override
    public void deleteItemById(@Positive long ownerId,
                               @Positive long itemId) throws NotFoundException {
        final ItemEntity itemEntity = itemRepository
                .findByIdAndOwnerId(itemId, ownerId)
                .orElseThrow(
                        () -> new NotFoundException("Вещь по id => " + itemId
                                + " не принадлежит пользователю с id => " + ownerId)
                );
        itemRepository.delete(itemEntity);
        log.info("Пользователем с id => {} удалена вещь с id => {}", ownerId, itemId);
    }

    @Override
    public List<ItemResponseDto> findItemsResponseDtoByOwnerId(@Positive long ownerId,
                                                               @PositiveOrZero int from,
                                                               @Positive int size) {
        final Page<ItemResponseDto> page = itemRepository
                .findAllByOwnerIdOrderById(ownerId, Util.getPageSortById(from, size))
                .map(ItemMapper::toItemDto);
        log.info("Вещи получены size => {}", page.getTotalElements());
        return page.getContent();
    }

    @Override
    public List<ItemWithBookingResponseDto> findItemsWithBookingResponseDtoByOwnerId(@Positive long ownerId,
                                                                                     @PositiveOrZero int from,
                                                                                     @Positive int size) {
        userService.checkUserIsExistById(ownerId);
        final Page<ItemWithBookingResponseDto> page = itemRepository
                .findByOwnerWithBooking(ownerId, LocalDateTime.now(), Util.getPageSortById(from, size))
                .map(ItemMapper::toItemWithBookingResponseDto);
        log.info("Вещи с бронированиями получены size => {}", page.getTotalElements());
        return page.getContent();

    }

    @Override
    public List<ItemResponseDto> findItemsIsNotRentedByNameOrDescription(@NotNull String text,
                                                                         @PositiveOrZero int from,
                                                                         @Positive int size) {
        if (text.isBlank()) {
            log.info("Получен пустой запрос поиска вещи");
            return Collections.emptyList();
        }
        final Page<ItemResponseDto> page = itemRepository
                .findItemIsNotRentedByNameOrDescription(text, Util.getPageSortById(from, size))
                .map(ItemMapper::toItemDto);
        log.info("Вещи получены size => {}, по запросу => {} ", page.getTotalElements(), text);
        return page.getContent();
    }

    @Transactional
    @Modifying
    @Validated
    @Override
    public CommentResponseDto createComment(@Valid CommentResponseDto commentResponseDto,
                                            @Positive long itemId,
                                            @Positive long bookerId) throws BadRequestException {
        if (!bookingService.checkBookingWithUserBookedItemStatusApproved(itemId, bookerId)) {
            throw new BadRequestException("Данный пользователь с id => " + bookerId
                    + " вещь по id => " + itemId + " не бронировал!");
        }

        final UserNameProjection authorName = userService.findNameByUserId(bookerId);

        final CommentResponseDto commentResponseDTO = CommentMapper.toCommentResponseDto(commentRepository.save(
                        CommentMapper.toCommentEntityAndCreatedNow(
                                commentResponseDto, itemId, bookerId)
                ), authorName.getName()
        );
        log.info("Пользователем с id => " + bookerId + " комментарий с id => " + commentResponseDTO.getId()
                + " добавлен к вещи c id => " + itemId);
        return commentResponseDTO;
    }

    @Override
    public ItemEntity findItemEntityById(@Positive long itemId) throws NotFoundException {
        log.info("Вещь по id => {} получена для СЕРВИСОВ", itemId);
        return itemRepository.findById(itemId)
                .orElseThrow(
                        () -> new NotFoundException("Вещь по id => "
                                + itemId + " не существует поиск СЕРВИСОВ")
                );
    }

    @Override
    public void checkItemIsExistById(@Positive long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Вещь по id => " + itemId + " не существует");
        }
    }


}
