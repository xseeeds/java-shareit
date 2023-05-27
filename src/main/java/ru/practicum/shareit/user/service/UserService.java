package ru.practicum.shareit.user.service;

import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoCreate;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Validated
public interface UserService {

    List<UserDto> getAllUserDto();

    UserDto getUserById(@Positive long userId);

    @Validated
    UserDto createUser(@Valid UserDtoCreate userDtoCreate);

    @Validated
    UserDto updateUser(@Positive long userId,
                    @Valid UserDto userDto);

    void deleteUser(@Positive long userId);
}
