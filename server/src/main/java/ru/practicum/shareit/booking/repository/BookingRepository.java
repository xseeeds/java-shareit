package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.BookingEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, Long>, QuerydslPredicateExecutor<BookingEntity> {

    BookingEntity findByIdAndItemOwnerId(long bookingId, long ownerId);

    @Query("SELECT b " +
            "FROM BookingEntity AS b " +
            "LEFT JOIN b.item AS i " +
            "WHERE b.id = ?1 " +
            "AND (b.booker.id = ?2 OR i.owner.id = ?2)")
    BookingEntity findByIdAndBookerIdOrItemOwnerId(long bookingId, long bookerId, long ownerId);

    boolean existsByItemIdAndBookerIdAndEndIsBeforeAndStatus(long itemId, long bookerId, LocalDateTime end, Status status);

    BookingShortDtoProjection findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(long itemId, LocalDateTime now, Status status);

    BookingShortDtoProjection findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(long itemId, LocalDateTime now, Status status);

    List<BookingEntity> findAllBookingByItemOwnerId(long ownerId);

}
