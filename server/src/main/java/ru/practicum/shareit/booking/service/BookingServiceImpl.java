package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.booking.model.QBookingEntity;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.repository.BookingShortDtoProjection;
import ru.practicum.shareit.exception.exp.BadRequestException;
import ru.practicum.shareit.exception.exp.NotFoundException;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;


    public BookingServiceImpl(BookingRepository bookingRepository,
                              @Lazy ItemService itemService,
                              @Lazy UserService userService) {
        this.bookingRepository = bookingRepository;
        this.itemService = itemService;
        this.userService = userService;
    }

    @Transactional
    @Modifying
    @Override
    public BookingResponseDto createBooking(long bookerId,
                                            BookingRequestDto bookingRequestDto) throws NotFoundException, BadRequestException {
        final ItemEntity itemEntity = itemService.findItemEntityById(bookingRequestDto.getItemId());
        if (!itemEntity.getAvailable()) {
            throw new BadRequestException("SERVICE => Вещь id => " + itemEntity.getId() + " не доступна для бронирования");
        }
        if (itemEntity.getOwnerId() == bookerId) {
            throw new NotFoundException("SERVICE => Пользователь id => " + bookerId
                    + " не может забронировать свою вещь id => " + itemEntity.getId());
        }

        userService.checkUserIsExistById(bookerId);

        final BookingResponseDto savedBooking = BookingMapper
                .toBookingResponseDto(bookingRepository.save(
                        BookingMapper
                                .toBookingAndStatusWaiting(bookingRequestDto, itemEntity, bookerId)));
        log.info("SERVICE => Создано бронирование с id => {} пользователем с id => {} на вещь с id => {}",
                savedBooking.getId(), bookerId, itemEntity.getId());
        return savedBooking;
    }

    @Transactional
    @Modifying
    @Override
    public BookingResponseDto approveBookingById(long ownerId,
                                                 long bookingId,
                                                 boolean approved) throws NotFoundException, BadRequestException {
        BookingEntity bookingEntity = bookingRepository.findByIdAndItemOwnerId(bookingId, ownerId);
        if (bookingEntity == null) {
            throw new NotFoundException("SERVICE => Пользователем по id => " + ownerId + " бронирование по id => " + bookingId + " не найдено");
        }
        if (!bookingEntity.getStatus().equals(Status.WAITING)) {
            throw new BadRequestException("SERVICE => Статус бронирования id => " + bookingId + " не WAITING!");
        }

        bookingEntity = bookingEntity
                .toBuilder()
                .status(approved ? Status.APPROVED : Status.REJECTED)
                .build();

        final BookingResponseDto savedBookingResponseDto = BookingMapper
                .toBookingResponseDto(
                        bookingRepository.save(bookingEntity));

        log.info("SERVICE => {} бронирование id => {}", approved ? "Approved" : "Rejected", bookingId);
        return savedBookingResponseDto;

    }

    @Override
    public BookingResponseDto findBookingById(long ownerId,
                                              long bookingId) throws NotFoundException {
        final BookingEntity bookingEntity = bookingRepository.findByIdAndBookerIdOrItemOwnerId(bookingId, ownerId, ownerId);
        if (bookingEntity == null) {
            throw new NotFoundException("SERVICE => Пользователем по id => " + ownerId + " бронирование по id => " + bookingId + " не найдено");
        }
        final BookingResponseDto bookingResponseDto = BookingMapper.toBookingResponseDto(bookingEntity);
        log.info("SERVICE => Пользователем с id => {} получено бронирование по id => {}", ownerId, bookingId);
        return bookingResponseDto;
    }

    @Override
    public List<BookingResponseDto> findBookings(long userId,
                                                 State state,
                                                 int from,
                                                 int size,
                                                 boolean bookerIdOrOwnerId) throws NotFoundException, BadRequestException {
        userService.checkUserIsExistById(userId);

        final List<Predicate> predicateList = new ArrayList<>();
        if (bookerIdOrOwnerId) {
            predicateList.add(QBookingEntity.bookingEntity.booker.id.eq(userId));
        } else {
            predicateList.add(QBookingEntity.bookingEntity.item.owner.id.eq(userId));
        }

        final LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                break;
            case CURRENT:
                predicateList.add(QBookingEntity.bookingEntity.start.before(now)
                        .and(QBookingEntity.bookingEntity.end.after(now)));
                break;
            case PAST:
                predicateList.add(QBookingEntity.bookingEntity.end.before(now));
                break;
            case FUTURE:
                predicateList.add(QBookingEntity.bookingEntity.start.after(now));
                break;
            case WAITING:
                predicateList.add(QBookingEntity.bookingEntity.status.eq(Status.WAITING));
                break;
            case REJECTED:
                predicateList.add(QBookingEntity.bookingEntity.status.eq(Status.REJECTED));
                break;
        }

        final Predicate totalPredicate = ExpressionUtils.allOf(predicateList);
        assert totalPredicate != null;

        final Page<BookingResponseDto> bookingResponsesPage = bookingRepository
                .findAll(totalPredicate,
                        Util.getPageSortDescByPropertiesAnd(from, size, "start"))
                .map(BookingMapper::toBookingResponseDto);
        log.info("SERVICE => Бронирования {} с id => {} , кол-во => {} получены",
                bookerIdOrOwnerId ? "Владелец" : "Пользователь", userId, bookingResponsesPage.getTotalElements());
        return bookingResponsesPage.getContent();
    }

    @Override
    public boolean checkExistsByItemIdAndBookerIdAndEndIsBeforeNowAndStatusApproved(long itemId,
                                                                                    long bookerId) {
        log.info("SERVICE => checkExistsByItemIdAndBookerIdAndEndIsBeforeNowAndStatusApproved item id => {}, bookerId => {}",
                itemId, bookerId);
        return bookingRepository.existsByItemIdAndBookerIdAndEndIsBeforeAndStatus(itemId,
                bookerId, LocalDateTime.now(), Status.APPROVED);
    }

    @Override
    public BookingShortResponseDto findLastBooking(long itemId,
                                                   LocalDateTime now) {
        final BookingShortDtoProjection bookingShortDtoProjection = bookingRepository
                .findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(itemId, now, Status.APPROVED);
        if (bookingShortDtoProjection == null) {
            log.info("SERVICE => Предыдущего бронирования для вещи по id => {} для СЕРВИСОВ не найдено", itemId);
            return null;
            //return BookingShortResponseDto.of();
        }
        final BookingShortResponseDto bookingShortDto =
                BookingMapper.toBookingShortResponseDto(bookingShortDtoProjection);
        log.info("SERVICE => Предыдущее бронирование по id => {} для СЕРВИСОВ получено для вещи по id => {}", bookingShortDto.getId(), itemId);
        return bookingShortDto;
    }

    @Override
    public BookingShortResponseDto findNextBooking(long itemId,
                                                   LocalDateTime now) {
        final BookingShortDtoProjection bookingShortDtoProjection = bookingRepository
                .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(itemId, now, Status.APPROVED);
        if (bookingShortDtoProjection == null) {
            log.info("SERVICE => Следующего бронирования для вещи по id => {} для СЕРВИСОВ не найдено", itemId);
            return null;
            //return BookingShortResponseDto.of();
        }
        final BookingShortResponseDto bookingShortDto =
                BookingMapper.toBookingShortResponseDto(bookingShortDtoProjection);
        log.info("SERVICE => Следующее бронирование по id => {} для СЕРВИСОВ получено для вещи по id => {}", bookingShortDto.getId(), itemId);
        return bookingShortDto;
    }

    @Override
    public List<BookingEntity> findAllBookingByItemOwnerId(long ownerId) {
        final List<BookingEntity> bookingEntityList = bookingRepository
                .findAllBookingByItemOwnerId(ownerId);
        log.info("SERVICE => Бронирования получены для СЕРВИСОВ size => {} владельца вещей по id => {}",
                bookingEntityList.size(), ownerId);
        return bookingEntityList;
    }

}
