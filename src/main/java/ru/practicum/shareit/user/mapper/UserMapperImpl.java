package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UserEntity;


@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toUserDto(UserEntity userEntity) {
        return UserDto
                .builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .build();
    }

    @Override
    public UserEntity toUser(UserDto userDto) {
        return UserEntity
                .builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

}