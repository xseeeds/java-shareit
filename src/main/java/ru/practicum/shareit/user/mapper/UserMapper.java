package ru.practicum.shareit.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.UserEntity;


@UtilityClass
public class UserMapper {

    public UserResponseDto toUserDto(UserEntity userEntity) {
        return UserResponseDto
                .builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .build();
    }

    public UserEntity toUser(UserResponseDto userResponseDto) {
        return UserEntity
                .builder()
                .name(userResponseDto.getName())
                .email(userResponseDto.getEmail())
                .build();
    }

}