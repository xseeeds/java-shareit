package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Value
@Builder
public class UserDto {

    Long id;

    @Pattern(regexp = "^\\S+$", message = "не должен быть пустым и содержать пробелы")
    String name;

    @Email
    String email;
}
