package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Generated
public class ItemRequestShortResponseDto {

    Long id;

    String description;

    LocalDateTime created;

    Long requesterId;
}
