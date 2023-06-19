package ru.practicum.shareit.booking.service;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.expception.exp.BadRequestException;
import ru.practicum.shareit.expception.exp.NotFoundException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Validated
@Transactional(readOnly = true)
public interface BookingService {

    @Validated
    @Transactional
    @Modifying
    BookingResponseDto createBooking(@Valid BookingRequestDto bookingRequestDto,
                                     @Positive long bookerId) throws NotFoundException, BadRequestException;

    @Transactional
    @Modifying
    BookingResponseDto approveBookingById(@Positive long ownerId,
                                          @Positive long bookingId,
                                          boolean approved) throws NotFoundException, BadRequestException;

    BookingResponseDto findBookingById(@Positive long ownerId,
                                       @Positive long bookingId) throws NotFoundException;

    List<BookingResponseDto> findBookings(@Positive long userId,
                                          String state,
                                          @PositiveOrZero int from,
                                          @Positive int size,
                                          boolean bookerIdOrOwnerId) throws NotFoundException;

    Optional<BookingEntity> findBookingWithUserBookedItemStatusApproved(@Positive long itemId,
                                                                        @Positive long bookerId);

    boolean checkBookingWithUserBookedItemStatusApproved(@Positive long itemId,
                                                         @Positive long bookerId);

    List<BookingShortResponseDto> findLastAndNextBookingEntity(@Positive long itemId, LocalDateTime now);

    BookingShortResponseDto findLastBooking(@Positive long itemId, LocalDateTime now);

    BookingShortResponseDto findNextBooking(@Positive long itemId, LocalDateTime now);

    List<BookingEntity> findAllBookingByItemOwnerId(@Positive long ownerId);

}
