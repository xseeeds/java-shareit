package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.item.model.ItemEntity;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<ItemEntity, Long>, QuerydslPredicateExecutor<ItemEntity> {

    Optional<ItemEntity> findByIdAndOwnerId(long itemId, long ownerId);

    boolean existsByIdAndOwnerId(long itemId, long ownerId);

    @Query(value = "select * from items as iE " +
            "where iE.available = true " +
            "and (iE.name ilike concat('%', ?1, '%') " +
            "or iE.description ilike concat('%', ?1, '%'))", nativeQuery = true)
    Page<ItemEntity> findItemIsAvailableByNameOrDescription(String text, Pageable page);

    Page<ItemEntity> findAllByOwnerId(long ownerId, Pageable page);

    List<ItemShortResponseDtoProjection> findAllByRequestIdIn(List<Long> requestsIds);

}
