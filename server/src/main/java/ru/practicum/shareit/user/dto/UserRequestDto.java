package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Generated;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@Generated
public class UserRequestDto {

    String name;

    String email;

}
