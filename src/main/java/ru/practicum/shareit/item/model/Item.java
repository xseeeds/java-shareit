package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Value;


/**
 * TODO Sprint add-controllers.
 */
@Value
@Builder(toBuilder = true)
public class Item {

    long id;

    String name;

    String description;

    Boolean available;

    long owner;
}
