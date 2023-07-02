package ru.practicum.shareit.item.dto;

import lombok.*;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Generated
public class CommentRequestDto {

    String text;

}
