package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UserEntity;

public interface UserMapper {

    UserDto toUserDto(UserEntity userEntity);

    UserEntity toUser(UserDto userDto);
}
