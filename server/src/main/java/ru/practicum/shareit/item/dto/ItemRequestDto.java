package ru.practicum.shareit.item.dto;

import lombok.*;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder(toBuilder = true)
@Generated
public class ItemRequestDto {

    String name;

    String description;

    Boolean available;

    Long requestId;

}
