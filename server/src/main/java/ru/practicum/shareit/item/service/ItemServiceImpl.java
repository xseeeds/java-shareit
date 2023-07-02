package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.exp.BadRequestException;
import ru.practicum.shareit.exception.exp.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.CommentEntity;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserNameProjection;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Util;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final BookingService bookingService;


    @Transactional
    @Modifying
    @Override
    public ItemResponseDto createItem(long ownerId,
                                      ItemRequestDto itemRequestDto) throws NotFoundException {

        userService.checkUserIsExistById(ownerId);

        final ItemResponseDto savedItem = ItemMapper.toItemResponseDto(
                itemRepository.save(
                        ItemMapper.toItemEntity(itemRequestDto, ownerId)));
        log.info("SERVICE => Пользователем с id => {} создана вещь с id => {}", ownerId, savedItem.getId());
        return savedItem;

    }

    @Override
    public ItemWithBookingAndCommentsResponseDto findItemWithBookingAndCommentsResponseDtoById(long userId,
                                                                                               long itemId,
                                                                                               int from,
                                                                                               int size) throws NotFoundException {
        final ItemEntity itemEntity = this.findItemEntityById(itemId);

        final List<CommentResponseDto> commentResponseDtoList = commentRepository
                .findAllByItemIdOrderByCreatedDesc(itemId, Util.getPageSortDescByPropertiesAnd(from, size, "created"))
                .stream()
                .map(commentEntity -> CommentMapper.toCommentResponseDto(
                        commentEntity, commentEntity.getAuthor().getName()))
                .collect(toList());

        ItemWithBookingAndCommentsResponseDto itemWithBookingAndCommentsResponseDto = ItemMapper
                .toItemWithCommentsResponseDto(itemEntity, commentResponseDtoList);

        boolean flag = false;

        if (itemEntity.getOwnerId().equals(userId)) {
            final BookingShortResponseDto lastBooking = bookingService.findLastBooking(itemId, LocalDateTime.now());
            final BookingShortResponseDto nextBooking = bookingService.findNextBooking(itemId, LocalDateTime.now());
            itemWithBookingAndCommentsResponseDto = itemWithBookingAndCommentsResponseDto
                    .toBuilder()
                    .lastBooking(lastBooking)
                    .nextBooking(nextBooking)
                    .build();
            flag = true;
        }
        log.info("SERVICE => {} по id => {} получил вещь с {} комментариями size => {} по id => {} получена",
                flag ? "Владелец" : "Пользователь", userId, flag ? "бронированием(предыдущее и следующее) и" : "",
                commentResponseDtoList.size(), itemId);
        return itemWithBookingAndCommentsResponseDto;
    }

    @Transactional
    @Modifying
    @Override
    public ItemResponseDto updateItem(long ownerId,
                                      long itemId,
                                      ItemRequestDto itemRequestDto) throws NotFoundException {
        ItemEntity itemEntity = itemRepository
                .findByIdAndOwnerId(itemId, ownerId)
                .orElseThrow(
                        () -> new NotFoundException("SERVICE => Вещь по id => " + itemId
                                + " не принадлежит пользователю с id => " + ownerId));

        itemEntity = itemEntity
                .toBuilder()
                .name(itemRequestDto.getName() != null ? itemRequestDto.getName() : itemEntity.getName())
                .description(itemRequestDto.getDescription() != null ?
                        itemRequestDto.getDescription() : itemEntity.getDescription())
                .available(itemRequestDto.getAvailable() != null ?
                        itemRequestDto.getAvailable() : itemEntity.getAvailable())
                .build();

        final ItemResponseDto savedItem = ItemMapper.toItemResponseDto(itemRepository.save(itemEntity));

        log.info("SERVICE => Пользователем с id => {} обновлена вещь с id => {}", ownerId, itemId);
        return savedItem;
    }

    @Transactional
    @Modifying
    @Override
    public void deleteItemById(long ownerId,
                               long itemId) throws NotFoundException {
        if (!itemRepository.existsByIdAndOwnerId(itemId, ownerId)) {
            throw new NotFoundException("SERVICE => Вещь по id => " + itemId
                    + " не принадлежит пользователю с id => " + ownerId);
        }
        itemRepository.deleteById(itemId);
        log.info("SERVICE => Пользователем с id => {} удалена вещь с id => {}", ownerId, itemId);
    }

    @Override
    public List<ItemWithBookingAndCommentsResponseDto> findItemWithBookingAndCommentsResponseDtoByOwnerId(long ownerId,
                                                                                                          int from,
                                                                                                          int size) throws NotFoundException {

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
        log.info("SERVICE => Запрос пользователя с id => {} вещи получены size => {}",ownerId, itemWithBookingAndCommentsResponseDtoPage.getTotalElements());
        return itemWithBookingAndCommentsResponseDtoPage.getContent();
    }

    @Override
    public List<ItemResponseDto> findItemIsAvailableByNameOrDescription(String text,
                                                                        int from,
                                                                        int size) {
        final Page<ItemResponseDto> itemResponseDtoPage = itemRepository
                .findItemIsAvailableByNameOrDescription(text, Util.getPageSortAscByProperties(from, size, "id"))
                .map(ItemMapper::toItemResponseDto);
        log.info("SERVICE => Вещи получены size => {}, по запросу => {} ", itemResponseDtoPage.getTotalElements(), text);
        return itemResponseDtoPage.getContent();
    }

    @Transactional
    @Modifying
    @Override
    public CommentResponseDto createComment(long bookerId,
                                            long itemId,
                                            CommentRequestDto commentRequestDto) throws BadRequestException, NotFoundException {
        if (!bookingService.checkExistsByItemIdAndBookerIdAndEndIsBeforeNowAndStatusApproved(itemId, bookerId)) {
            throw new BadRequestException("SERVICE => Данный пользователь с id => " + bookerId
                    + " вещь по id => " + itemId + " не бронировал!");
        }

        final UserNameProjection authorName = userService.findNameByUserId(bookerId);

        final CommentResponseDto commentResponseDTO = CommentMapper.toCommentResponseDto(commentRepository.save(
                        CommentMapper.toCommentEntityAndCreatedNow(
                                commentRequestDto, itemId, bookerId)),
                authorName.getName());
        log.info("SERVICE => Пользователем с id => " + bookerId + " добавлен комментарий с id => " + commentResponseDTO.getId()
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
        log.info("SERVICE => Список вещей получен size => {} для СЕРВИСОВ", itemShortResponseDtoList.size());
        return itemShortResponseDtoList;
    }

    @Override
    public ItemEntity findItemEntityById(long itemId) throws NotFoundException {
        log.info("SERVICE => запрос вещи по id => {} для СЕРВИСОВ", itemId);
        return itemRepository.findById(itemId)
                .orElseThrow(
                        () -> new NotFoundException("SERVICE => Вещь по id => "
                                + itemId + " не существует поиск СЕРВИСОВ"));
    }

    @Override
    public void checkItemIsExistById(long itemId) throws NotFoundException {
        log.info("SERVICE => Запрос существует вещь по id => {} для СЕРВИСОВ", itemId);
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("SERVICE => Вещь по id => " + itemId + " не существует");
        }
    }


}
