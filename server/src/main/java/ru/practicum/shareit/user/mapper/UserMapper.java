package ru.practicum.shareit.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.UserEntity;


@UtilityClass
public class UserMapper {

    public UserResponseDto toUserResponseDto(UserEntity userEntity) {
        return UserResponseDto
                .builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .build();
    }

    public UserEntity toUserEntity(UserRequestDto userRequestDto) {
        return UserEntity
                .builder()
                .name(userRequestDto.getName())
                .email(userRequestDto.getEmail())
                .build();
    }

}