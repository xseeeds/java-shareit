package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Value
@Builder(toBuilder = true)
public class ItemDto {

    @Positive(groups = Marker.OnUpdate.class)
    Long id;

    @NotNull(groups = Marker.OnCreate.class)
    @NotBlank(groups = Marker.OnCreate.class)
    String name;

    @NotNull(groups = Marker.OnCreate.class)
    @NotBlank(groups = Marker.OnCreate.class)
    String description;

    @NotNull(groups = Marker.OnCreate.class)
    Boolean available;

}
