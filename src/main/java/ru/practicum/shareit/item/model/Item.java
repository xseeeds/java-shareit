package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Item {

    long id;

    String name;

    String description;

    Boolean available;

    long owner;
}
