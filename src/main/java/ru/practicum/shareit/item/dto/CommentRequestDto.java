package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Generated
public class CommentRequestDto {

    @NotNull
    @NotBlank
    String text;

}
