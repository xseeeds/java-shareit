package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.item.model.CommentEntity;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long>, QuerydslPredicateExecutor<CommentEntity> {

    List<CommentEntity> findAllByItemIdOrderByCreatedDesc(long itemId);

    List<CommentEntity> findAllByItemIdIn(List<Long> itemIds);

}