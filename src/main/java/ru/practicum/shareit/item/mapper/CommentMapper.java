package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.CommentEntity;

import java.time.LocalDateTime;


@UtilityClass
public class CommentMapper {

    public CommentResponseDto toCommentResponseDto(CommentEntity commentEntity, String authorName) {
        return CommentResponseDto
                .builder()
                .id(commentEntity.getId())
                .authorName(authorName)
                .created(commentEntity.getCreated())
                .text(commentEntity.getText())
                .build();
    }

    public CommentEntity toCommentEntityAndCreatedNow(CommentResponseDto commentResponseDto, long itemId, long authorId) {
        return CommentEntity
                .builder()
                .text(commentResponseDto.getText())
                .itemId(itemId)
                .authorId(authorId)
                .created(LocalDateTime.now())
                .build();
    }
}
