package ru.practicum.shareit.request.dto;


import lombok.*;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Generated
public class ItemRequestResponseDto {

    Long id;

    String description;

    UserResponseDto requester;

    LocalDateTime created;

    List<ItemShortResponseDto> items;

}
