package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.request.model.ItemRequestEntity;


public interface ItemRequestRepository extends JpaRepository<ItemRequestEntity, Long>, QuerydslPredicateExecutor<ItemRequestEntity> {

    Page<ItemRequestEntity> findAllByRequesterIdOrderByCreatedDesc(long requesterId, Pageable page);

    Page<ItemRequestEntity> findAllByRequesterIdIsNotOrderByCreatedDesc(long requesterId, Pageable page);

}
