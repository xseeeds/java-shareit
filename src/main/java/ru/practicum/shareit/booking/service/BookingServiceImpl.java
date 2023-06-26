package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
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
import ru.practicum.shareit.expception.exp.BadRequestException;
import ru.practicum.shareit.expception.exp.NotFoundException;
import ru.practicum.shareit.expception.exp.UnknownBookingStateException;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Util;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Validated
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


    @Validated
    @Transactional
    @Modifying
    @Override
    public BookingResponseDto createBooking(@Positive long bookerId,
                                            @Valid BookingRequestDto bookingRequestDto) throws NotFoundException, BadRequestException {
        final ItemEntity itemEntity = itemService.findItemEntityById(bookingRequestDto.getItemId());
        if (!itemEntity.getAvailable()) {
            throw new BadRequestException("Вещь id => " + itemEntity.getId() + " не доступна для бронирования");
        }
        if (itemEntity.getOwnerId() == bookerId) {
            throw new NotFoundException("Пользователь id => " + bookerId + " не может забронировать свою вещь id => " + itemEntity.getId());
        }

        userService.checkUserIsExistById(bookerId);

        final BookingResponseDto savedBooking = BookingMapper
                .toBookingResponseDto(bookingRepository.save(
                        BookingMapper
                                .toBookingAndStatusWaiting(bookingRequestDto, itemEntity, bookerId)));
        log.info("Создано бронирование с id => {} пользователем с id => {} на вещь с id => {}", savedBooking.getId(), bookerId, itemEntity.getId());
        return savedBooking;
    }

    @Transactional
    @Modifying
    @Override
    public BookingResponseDto approveBookingById(@Positive long ownerId,
                                                 @Positive long bookingId,
                                                 boolean approved) throws NotFoundException, BadRequestException {
        BookingEntity bookingEntity = bookingRepository.findByIdAndItemOwnerId(bookingId, ownerId);
        if (bookingEntity == null) {
            throw new NotFoundException("Пользователем по id => " + ownerId + " бронирование по id => " + bookingId + " не найдено");
        }
        if (!bookingEntity.getStatus().equals(Status.WAITING)) {
            throw new BadRequestException("Статус бронирования id => " + bookingId + " не WAITING!");
        }

        bookingEntity = bookingEntity
                .toBuilder()
                .status(approved ? Status.APPROVED : Status.REJECTED)
                .build();

        final BookingResponseDto savedBookingResponseDto = BookingMapper
                .toBookingResponseDto(
                        bookingRepository.save(bookingEntity));

        log.info((approved ? "Approved" : "Rejected") + " бронирование id => {}", bookingId);
        return savedBookingResponseDto;

    }

    @Override
    public BookingResponseDto findBookingById(@Positive long ownerId,
                                              @Positive long bookingId) throws NotFoundException {
        BookingEntity bookingEntity = bookingRepository.findByIdAndBookerIdOrItemOwnerId(bookingId, ownerId, ownerId);
        if (bookingEntity == null) {
            throw new NotFoundException("Пользователем по id => " + ownerId + " бронирование по id => " + bookingId + " не найдено");
        }
        final BookingResponseDto bookingResponseDto = BookingMapper.toBookingResponseDto(bookingEntity);
        log.info("Пользвотелем с id => {} получено бронирование по id => {}", ownerId, bookingId);
        return bookingResponseDto;
    }

    @Override
    public List<BookingResponseDto> findBookings(@Positive long userId,
                                                 @NotNull String state,
                                                 @PositiveOrZero int from,
                                                 @Positive int size,
                                                 boolean bookerIdOrOwnerId) throws NotFoundException, BadRequestException {
        final State s;
        try {
            s = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnknownBookingStateException("Unknown state: " + state);
        }

        userService.checkUserIsExistById(userId);

        final LocalDateTime now = LocalDateTime.now();
        final List<Predicate> predicateList = new ArrayList<>();

        if (bookerIdOrOwnerId) {
            predicateList.add(QBookingEntity.bookingEntity.booker.id.eq(userId));
        } else {
            predicateList.add(QBookingEntity.bookingEntity.item.owner.id.eq(userId));
        }

        switch (s) {
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
                        Util.getPageSortDescByProperties(from, size, "start"))
                .map(BookingMapper::toBookingResponseDto);
        log.info("Бронирования пользователя с id => {} , bookerOrOwner {} id => {}, кол-во => {} получены", userId,
                bookerIdOrOwnerId ? "booker" : "owner", userId, bookingResponsesPage.getTotalElements());
        return bookingResponsesPage.getContent();
    }

    @Override
    public boolean checkExistsByItemIdAndBookerIdAndEndIsBeforeNowAndStatusApproved(@Positive long itemId,
                                                                                    @Positive long bookerId) {
        log.info("checkExistsByItemIdAndBookerIdAndEndIsBeforeNowAndStatusApproved item id => {}, bookerId => {}",
                itemId, bookerId);
        return bookingRepository.existsByItemIdAndBookerIdAndEndIsBeforeAndStatus(itemId,
                bookerId, LocalDateTime.now(), Status.APPROVED);
    }

    @Override
    public BookingShortResponseDto findLastBooking(@Positive long itemId, @NotNull LocalDateTime now) {
        final BookingShortDtoProjection bookingShortDtoProjection = bookingRepository
                .findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(itemId, now, Status.APPROVED);
        if (bookingShortDtoProjection == null) {
            log.info("Предыдущего бронирования для вещи по id => {} для СЕРВИСОВ не найдено", itemId);
            return null;
        }
        final BookingShortResponseDto bookingShortDto =
                BookingMapper.toBookingShortResponseDto(bookingShortDtoProjection);
        log.info("Предыдущее бронирование по id => {} для СЕРВИСОВ получено для вещи по id => {}", bookingShortDto.getId(), itemId);
        return bookingShortDto;
    }

    @Override
    public BookingShortResponseDto findNextBooking(@Positive long itemId, @NotNull LocalDateTime now) {
        final BookingShortDtoProjection bookingShortDtoProjection = bookingRepository
                .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(itemId, now, Status.APPROVED);
        if (bookingShortDtoProjection == null) {
            log.info("Следующего бронирования для вещи по id => {} для СЕРВИСОВ не найдено", itemId);
            return null;
        }
        final BookingShortResponseDto bookingShortDto =
                BookingMapper.toBookingShortResponseDto(bookingShortDtoProjection);
        log.info("Следующее бронирование по id => {} для СЕРВИСОВ получено для вещи по id => {}", bookingShortDto.getId(), itemId);
        return bookingShortDto;
    }

    @Override
    public List<BookingEntity> findAllBookingByItemOwnerId(@Positive long ownerId) {
        final List<BookingEntity> bookingEntityList = bookingRepository
                .findAllBookingByItemOwnerId(ownerId);
        log.info("Бронирования получены для СЕРВИСОВ size => {} владельца вещей по id => {}",
                bookingEntityList.size(), ownerId);
        return bookingEntityList;
    }

}
