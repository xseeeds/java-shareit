package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.expception.exp.BadRequestException;
import ru.practicum.shareit.expception.exp.NotFoundException;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsResponseDto;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
        final ItemResponseDto savedItem = ItemMapper.toItemResponseDto(
                itemRepository.save(
                        ItemMapper.toItemEntity(itemResponseDto, ownerId)
                )
        );
        log.info("Пользователем с id => {} создана вещь с id => {}", ownerId, savedItem.getId());
        return savedItem;

    }

    @Override
    public ItemResponseDto findItemDtoById(@Positive long itemId) throws NotFoundException {
        final ItemResponseDto foundItem = ItemMapper.toItemResponseDto(
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
        final ItemEntity itemEntity = this.findItemEntityById(itemId);

        final List<CommentResponseDto> commentResponseDtoList = commentRepository
                .findAllByItemIdOrderByCreatedDesc(itemId)
                .stream()
                .map(commentEntity -> CommentMapper.toCommentResponseDto(
                        commentEntity, commentEntity.getAuthor().getName()))
                .collect(toList());

        final ItemWithBookingAndCommentsResponseDto itemWithBookingAndCommentsResponseDto = ItemMapper
                .toItemWithCommentsResponseDto(itemEntity, commentResponseDtoList);

        if (itemEntity.getOwner().getId().equals(userId)) {
            final BookingShortResponseDto lastBooking = bookingService.findLastBooking(itemId, LocalDateTime.now());
            final BookingShortResponseDto nextBooking = bookingService.findNextBooking(itemId, LocalDateTime.now());
            itemWithBookingAndCommentsResponseDto.setLastBooking(lastBooking);
            itemWithBookingAndCommentsResponseDto.setNextBooking(nextBooking);
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
        final ItemResponseDto savedItem = ItemMapper.toItemResponseDto(itemEntity);
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
    public List<ItemResponseDto> findItemsResponseDtoByOwnerId(@Positive long ownerId,
                                                               @PositiveOrZero int from,
                                                               @Positive int size) {
        final List<ItemResponseDto> page = itemRepository
                .findAllByOwnerIdOrderById(ownerId)
                .stream()
                .map(ItemMapper::toItemResponseDto)
                .collect(toList());
        log.info("Вещи получены size => {}", page.size());
        return page;
    }

    @Override
    public List<ItemWithBookingAndCommentsResponseDto> findItemWithBookingAndCommentsResponseDtoByOwnerId(@Positive long ownerId,
                                                                                                          @PositiveOrZero int from,
                                                                                                          @Positive int size) {
        userService.checkUserIsExistById(ownerId);
        final List<BookingEntity> bookingEntityList = bookingService
                .findAllBookingByItemOwnerId(ownerId);
        final List<ItemEntity> itemEntityList = itemRepository
                .findAllByOwnerIdOrderById(ownerId);
        final List<Long> itemIds = itemEntityList
                .stream()
                .map(ItemEntity::getId)
                .collect(toList());
        final List<CommentEntity> commentEntityList = commentRepository
                .findAllByItemIdIn(itemIds);

        final List<ItemWithBookingAndCommentsResponseDto> itemWithBookingAndCommentsResponseDtoList = itemEntityList
                .stream()
                .map(itemEntity -> {
                    BookingShortResponseDto lastBooking = null;
                    BookingShortResponseDto nextBooking = null;

                    final List<CommentResponseDto> commentEntityListByItemIdOrderByCreatedDesc = commentEntityList
                            .stream()
                            .filter(commentEntity -> commentEntity.getItemId().equals(itemEntity.getId()))
                            .sorted(Comparator
                                    .comparing(CommentEntity::getCreated).reversed())
                            .map(commentEntity -> CommentMapper
                                    .toCommentResponseDto(commentEntity, commentEntity.getAuthor().getName()))
                            .collect(toList());

                    final LocalDateTime now = LocalDateTime.now();

                    if (!bookingEntityList.isEmpty()) {

                        final Optional<BookingEntity> bookingLast = bookingEntityList
                                .stream()
                                .filter(bookingEntity ->
                                        bookingEntity.getItem().getId().equals(itemEntity.getId())
                                                && bookingEntity.getStatus().equals(Status.APPROVED)
                                                && bookingEntity.getStart().isBefore(now))
                                .max(Comparator
                                        .comparing(BookingEntity::getStart));

                        final Optional<BookingEntity> bookingNext = bookingEntityList
                                .stream()
                                .filter(bookingEntity ->
                                        bookingEntity.getItem().getId().equals(itemEntity.getId())
                                                && bookingEntity.getStatus().equals(Status.APPROVED)
                                                && bookingEntity.getStart().isAfter(now))
                                .min(Comparator
                                        .comparing(BookingEntity::getStart));

                        if (bookingLast.isPresent()) {
                            lastBooking = BookingMapper.toBookingShortResponseDto(bookingLast.get());
                        }
                        if (bookingNext.isPresent()) {
                            nextBooking = BookingMapper.toBookingShortResponseDto(bookingNext.get());
                        }

                    }
                    return ItemMapper
                            .toItemWithBookingAndCommentsResponseDto(
                                    itemEntity, lastBooking, nextBooking, commentEntityListByItemIdOrderByCreatedDesc);
                })
                .collect(toList());
        log.info("Вещи получены size => {}", itemWithBookingAndCommentsResponseDtoList.size());
        return itemWithBookingAndCommentsResponseDtoList;
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
                .map(ItemMapper::toItemResponseDto);
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
