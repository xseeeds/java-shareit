package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class ItemDto {

    Long id;

    String name;

    String description;

    Boolean available;
}
