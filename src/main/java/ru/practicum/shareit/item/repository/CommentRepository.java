package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.CommentEntity;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    List<CommentEntity> findAllByItemIdOrderByCreatedDesc(long itemId, Pageable page);

    List<CommentEntity> findAllByItemIdIn(List<Long> itemIds);

}