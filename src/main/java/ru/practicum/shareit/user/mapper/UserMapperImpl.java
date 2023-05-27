package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoCreate;
import ru.practicum.shareit.user.model.User;


@Component
public class UserMapperImpl implements UserMapper {

    private long generatorId = 0;

    @Override
    public UserDto toUserDto(User user) {
        return UserDto
                .builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Override
    public User toUserSetNextId(UserDtoCreate userDtoCreate) {
        return User
                .builder()
                .id(getNextGeneratorId())
                .name(userDtoCreate.getName())
                .email(userDtoCreate.getEmail())
                .build();
    }

    private long getNextGeneratorId() {
        return ++generatorId;
    }
}