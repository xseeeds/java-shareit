package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.*;

@Value
@Builder(toBuilder = true)
public class UserDto {

    @Positive(groups = Marker.OnUpdate.class)
    Long id;

    @NotNull(groups = Marker.OnCreate.class)
    @Pattern(groups = {Marker.OnCreate.class, Marker.OnUpdate.class},
            regexp = "^\\S+$",
            message = "не должен быть пустым и содержать пробелы")
    String name;

    @NotBlank(groups = Marker.OnCreate.class)
    @Email(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    String email;
}
