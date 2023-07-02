package ru.practicum.shareit.item.dto.item;

import lombok.*;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder(toBuilder = true)
@Generated
public class ItemResponseDto {

    Long id;

    String name;

    String description;

    Boolean available;

    Long requestId;

}
