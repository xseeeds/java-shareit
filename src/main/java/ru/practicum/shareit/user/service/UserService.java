package ru.practicum.shareit.user.service;

import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Validated
public interface UserService {

    List<UserDto> getAllUserDto();

    UserDto getUserById(@Positive long userId);

    @Validated(Marker.OnCreate.class)
    UserDto createUser(@Valid UserDto userDto);

    @Validated(Marker.OnUpdate.class)
    UserDto updateUser(@Positive long userId,
                       @Valid UserDto userDto);

    void deleteUser(@Positive long userId);
}
