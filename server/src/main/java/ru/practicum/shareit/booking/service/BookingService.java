package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.exception.exp.BadRequestException;
import ru.practicum.shareit.exception.exp.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    BookingResponseDto createBooking(long bookerId,
                                     BookingRequestDto bookingRequestDto) throws NotFoundException, BadRequestException;

    BookingResponseDto approveBookingById(long ownerId,
                                          long bookingId,
                                          boolean approved) throws NotFoundException, BadRequestException;

    BookingResponseDto findBookingById(long ownerId,
                                       long bookingId) throws NotFoundException;

    List<BookingResponseDto> findBookings(long userId,
                                          State state,
                                          int from,
                                          int size,
                                          boolean bookerIdOrOwnerId) throws NotFoundException, BadRequestException;

    boolean checkExistsByItemIdAndBookerIdAndEndIsBeforeNowAndStatusApproved(long itemId,
                                                                             long bookerId);

    BookingShortResponseDto findLastBooking(long itemId,
                                            LocalDateTime now);

    BookingShortResponseDto findNextBooking(long itemId,
                                            LocalDateTime now);

    List<BookingEntity> findAllBookingByItemOwnerId(long ownerId);

}
