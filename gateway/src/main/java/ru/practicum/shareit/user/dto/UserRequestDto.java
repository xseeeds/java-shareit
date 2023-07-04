package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Generated;
import lombok.Value;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Value
@Builder(toBuilder = true)
@Generated
public class UserRequestDto {

    @NotNull(groups = Marker.OnCreate.class)
    @Pattern(groups = {Marker.OnCreate.class, Marker.OnUpdate.class},
            regexp = "^\\S+$",
            message = "не должен быть пустым и содержать пробелы")
    String name;

    @NotBlank(groups = Marker.OnCreate.class)
    @Email(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    String email;

}
