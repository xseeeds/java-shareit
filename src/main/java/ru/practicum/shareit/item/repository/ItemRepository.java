package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.item.model.ItemEntity;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<ItemEntity, Long>, QuerydslPredicateExecutor<ItemEntity> {

    Optional<ItemEntity> findByIdAndOwnerId(long itemId, long ownerId);

    Page<ItemEntity> findAllByOwnerIdOrderById(long ownerId, Pageable page);

    @Query(value = "select * from items as iE " +
            "where iE.available = true " +
            "and (iE.name ilike concat('%', ?1, '%') " +
            "or iE.description ilike concat('%', ?1, '%'))", nativeQuery = true)
    Page<ItemEntity> findItemIsNotRentedByNameOrDescription(String text, Pageable page);

    Page<ItemEntity> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(String nameSearch,
                                                                                                       String descSearch,
                                                                                                       Pageable page);

    @Query(value =
            "SELECT " +
                "items_bookings.id, " +
                "items_bookings.name, " +
                "items_bookings.description, " +
                "items_bookings.available, " +
                "items_bookings.last_booking_entity AS lastBookingEntityId, " +
                "items_bookings.next_booking_entity AS nextBookingEntityId, " +
                "b1.booker_id AS lastBookingEntityBookerId, " +
                "b1.start_date AS lastBookingEntityStart, " +
                "b1.end_date AS lastBookingEntityEnd, " +
                "b2.booker_id AS nextBookingEntityBookerId, " +
                "b2.start_date AS nextBookingEntityStart, " +
                "b2.end_date AS nextBookingEntityEnd " +
            "FROM " +
                "( " +
                    "SELECT items.id, items.name, items.description, items.available, " +
                        "(" +
                            "SELECT MAX(b3.id) " +
                                "FROM bookings as b3 " +
                                "WHERE start_date in " +
                                                    "(" +
                                                    "SELECT MAX(start_date) " +
                                                        "FROM bookings " +
                                                        "WHERE status ilike 'APPROVED' AND start_date <= :now" +
                                                    ") " +
                                "AND b3.item_id = items.id" +
                        ") AS last_booking_entity, " +
                        "(" +
                            "SELECT MIN(b4.id) " +
                                "FROM bookings as b4 " +
                                "WHERE start_date in " +
                                                    "(" +
                                                    "SELECT MIN(START_DATE) " +
                                                        "FROM bookings " +
                                                        "WHERE status ilike 'APPROVED' AND start_date >= :now" +
                                                    ") " +
                                "AND b4.item_id = items.id" +
                        ") AS next_booking_entity " +
                    "FROM items " +
                    "WHERE OWNER_ID = :ownerId" +
                ") AS items_bookings " +
            "LEFT JOIN bookings AS b1 ON b1.id = items_bookings.last_booking_entity " +
            "LEFT JOIN bookings AS b2 ON b2.id = items_bookings.next_booking_entity ", nativeQuery = true)
    Page<ItemWithBookingProjection> findByOwnerWithBooking(long ownerId, LocalDateTime now, Pageable page);

}
