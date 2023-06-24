package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.expception.exp.BadRequestException;
import ru.practicum.shareit.expception.exp.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.CommentEntity;
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
import java.util.*;

import static java.util.stream.Collectors.*;

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
                                      @Valid ItemResponseDto itemResponseDto) throws NotFoundException {
        userService.checkUserIsExistById(ownerId);
        final ItemResponseDto savedItem = ItemMapper.toItemResponseDto(
                itemRepository.save(
                        ItemMapper.toItemEntity(itemResponseDto, ownerId)));
        log.info("Пользователем с id => {} создана вещь с id => {}", ownerId, savedItem.getId());
        return savedItem;

    }

    @Override
    public ItemWithBookingAndCommentsResponseDto findItemWithBookingAndCommentsResponseDtoById(@Positive long userId,
                                                                                               @Positive long itemId,
                                                                                               @PositiveOrZero int from,
                                                                                               @Positive int size) throws NotFoundException {
        final ItemEntity itemEntity = this.findItemEntityById(itemId);

        final List<CommentResponseDto> commentResponseDtoList = commentRepository
                .findAllByItemIdOrderByCreatedDesc(itemId, Util.getPageSortDescByProperties(from, size, "created"))
                .stream()
                .map(commentEntity -> CommentMapper.toCommentResponseDto(
                        commentEntity, commentEntity.getAuthor().getName()))
                .collect(toList());

        ItemWithBookingAndCommentsResponseDto itemWithBookingAndCommentsResponseDto = ItemMapper
                .toItemWithCommentsResponseDto(itemEntity, commentResponseDtoList);

        if (itemEntity.getOwnerId().equals(userId)) {
            final BookingShortResponseDto lastBooking = bookingService.findLastBooking(itemId, LocalDateTime.now());
            final BookingShortResponseDto nextBooking = bookingService.findNextBooking(itemId, LocalDateTime.now());
            itemWithBookingAndCommentsResponseDto = itemWithBookingAndCommentsResponseDto
                    .toBuilder()
                    .lastBooking(lastBooking)
                    .nextBooking(nextBooking)
                    .build();
            log.info("Владелец по id => {}", userId);
        }
        log.info("Пользователь по id => {} выполнил запрос findItemById, вещь с бронированием и комментариями по id => {} получена", userId, itemId);
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
                        () -> new NotFoundException("Вещь по id => " + itemId
                                + " не принадлежит пользователю с id => " + ownerId));

        itemEntity = itemEntity
                .toBuilder()
                .name(itemResponseDto.getName() != null ? itemResponseDto.getName() : itemEntity.getName())
                .description(itemResponseDto.getDescription() != null ?
                        itemResponseDto.getDescription() : itemEntity.getDescription())
                .available(itemResponseDto.getAvailable() != null ?
                        itemResponseDto.getAvailable() : itemEntity.getAvailable())
                .build();

        final ItemResponseDto savedItem = ItemMapper.toItemResponseDto(itemRepository.save(itemEntity));

        log.info("Пользователем с id => {} обновлена вещь с id => {}", ownerId, itemId);
        return savedItem;
    }

    @Transactional
    @Modifying
    @Override
    public void deleteItemById(@Positive long ownerId,
                               @Positive long itemId) throws NotFoundException {
        if (!itemRepository.existsByIdAndOwnerId(itemId, ownerId)) {
            throw new NotFoundException("Вещь по id => " + itemId
                    + " не принадлежит пользователю с id => " + ownerId);
        }
        itemRepository.deleteById(itemId);
        log.info("Пользователем с id => {} удалена вещь с id => {}", ownerId, itemId);
    }

    @Override
    public List<ItemWithBookingAndCommentsResponseDto> findItemWithBookingAndCommentsResponseDtoByOwnerId(@Positive long ownerId,
                                                                                                          @PositiveOrZero int from,
                                                                                                          @Positive int size) throws NotFoundException {

        //TODO подумай насчёт Pageable page comment

        userService.checkUserIsExistById(ownerId);
        final Map<Long, List<BookingEntity>> allBookingEntityMap = bookingService
                .findAllBookingByItemOwnerId(ownerId)
                .stream()
                .collect(groupingBy(
                        BookingEntity::getItemId, toList()));
        final Page<ItemEntity> itemEntityList = itemRepository
                .findAllByOwnerId(ownerId, Util.getPageSortAscByProperties(from, size, "id"));
        final List<Long> itemIds = itemEntityList
                .stream()
                .map(ItemEntity::getId)
                .collect(toList());
        final Map<Long, List<CommentResponseDto>> allCommentEntityMap = commentRepository
                .findAllByItemIdIn(itemIds)
                .stream()
                .sorted(Comparator
                        .comparing(CommentEntity::getCreated).reversed())
                .collect(groupingBy(
                        CommentEntity::getItemId, mapping(
                                commentEntity -> CommentMapper.toCommentResponseDto(
                                        commentEntity, commentEntity.getAuthor().getName()),
                                toList())));

        final Page<ItemWithBookingAndCommentsResponseDto> itemWithBookingAndCommentsResponseDtoPage = itemEntityList
                .map(itemEntity -> {
                    final List<CommentResponseDto> commentResponseDtoListByItemIdOrderByCreatedDesc = allCommentEntityMap
                            .getOrDefault(itemEntity.getId(), Collections.emptyList());

                    final LocalDateTime now = LocalDateTime.now();
                    final List<BookingEntity> bookingEntityList = allBookingEntityMap
                            .getOrDefault(itemEntity.getId(), Collections.emptyList());

                    BookingShortResponseDto lastBooking = null;
                    BookingShortResponseDto nextBooking = null;

                    if (!bookingEntityList.isEmpty()) {
                        final BookingEntity bookingLast = bookingEntityList
                                .stream()
                                .filter(bookingEntity ->
                                        bookingEntity.getStatus().equals(Status.APPROVED)
                                                && bookingEntity.getStart().isBefore(now))
                                .max(Comparator
                                        .comparing(BookingEntity::getStart))
                                .orElse(null);

                        if (bookingLast != null) {
                            lastBooking = BookingMapper.toBookingShortResponseDto(bookingLast);
                        }

                        final BookingEntity bookingNext = bookingEntityList
                                .stream()
                                .filter(bookingEntity ->
                                        bookingEntity.getStatus().equals(Status.APPROVED)
                                                && bookingEntity.getStart().isAfter(now))
                                .min(Comparator
                                        .comparing(BookingEntity::getStart))
                                .orElse(null);

                        if (bookingNext != null) {
                            nextBooking = BookingMapper.toBookingShortResponseDto(bookingNext);
                        }

                    }
                    return ItemMapper
                            .toItemWithBookingAndCommentsResponseDto(
                                    itemEntity, lastBooking, nextBooking, commentResponseDtoListByItemIdOrderByCreatedDesc);
                });
        log.info("Вещи получены size => {}", itemWithBookingAndCommentsResponseDtoPage.getTotalElements());
        return itemWithBookingAndCommentsResponseDtoPage.getContent();
    }

    @Override
    public List<ItemResponseDto> findItemIsAvailableByNameOrDescription(@NotNull String text,
                                                                        @PositiveOrZero int from,
                                                                        @Positive int size) {
        if (text.isBlank()) {
            log.info("Получен пустой запрос поиска вещи");
            return Collections.emptyList();
        }
        final Page<ItemResponseDto> itemResponseDtoPage = itemRepository
                .findItemIsAvailableByNameOrDescription(text, Util.getPageSortAscByProperties(from, size, "id"))
                .map(ItemMapper::toItemResponseDto);
        log.info("Вещи получены size => {}, по запросу => {} ", itemResponseDtoPage.getTotalElements(), text);
        return itemResponseDtoPage.getContent();
    }

    @Transactional
    @Modifying
    @Validated
    @Override
    public CommentResponseDto createComment(@Positive long bookerId,
                                            @Positive long itemId,
                                            @Valid CommentRequestDto commentRequestDto) throws BadRequestException, NotFoundException {
        if (!bookingService.checkExistsByItemIdAndBookerIdAndEndIsBeforeNowAndStatusApproved(itemId, bookerId)) {
            throw new BadRequestException("Данный пользователь с id => " + bookerId
                    + " вещь по id => " + itemId + " не бронировал!");
        }

        final UserNameProjection authorName = userService.findNameByUserId(bookerId);

        final CommentResponseDto commentResponseDTO = CommentMapper.toCommentResponseDto(commentRepository.save(
                        CommentMapper.toCommentEntityAndCreatedNow(
                                commentRequestDto, itemId, bookerId)),
                authorName.getName());
        log.info("Пользователем с id => " + bookerId + " добавлен комментарий с id => " + commentResponseDTO.getId()
                + " к вещи c id => " + itemId);
        return commentResponseDTO;
    }

    @Override
    public List<ItemShortResponseDto> findItemShortResponseDtoByRequestIdIn(List<Long> requestIds) {
        final List<ItemShortResponseDto> itemShortResponseDtoList = itemRepository
                .findAllByRequestIdIn(requestIds)
                .stream()
                .map(ItemMapper::toItemShortResponseDto)
                .collect(toList());
        log.info("Список вещей получен size => {} для СЕРВИСОВ", itemShortResponseDtoList.size());
        return itemShortResponseDtoList;
    }

    @Override
    public ItemEntity findItemEntityById(@Positive long itemId) throws NotFoundException {
        log.info("Вещь по id => {} получена для СЕРВИСОВ", itemId);
        return itemRepository.findById(itemId)
                .orElseThrow(
                        () -> new NotFoundException("Вещь по id => "
                                + itemId + " не существует поиск СЕРВИСОВ"));
    }

    @Override
    public void checkItemIsExistById(@Positive long itemId) throws NotFoundException {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Вещь по id => " + itemId + " не существует");
        }
    }


}
