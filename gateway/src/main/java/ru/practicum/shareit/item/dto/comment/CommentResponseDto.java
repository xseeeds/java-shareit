package ru.practicum.shareit.item.dto.comment;

import lombok.*;

import java.time.LocalDateTime;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Generated
public class CommentResponseDto {

	Long id;

	String text;

	String authorName;

	LocalDateTime created;

}