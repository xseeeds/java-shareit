package ru.practicum.shareit.user.service;

import ru.practicum.shareit.expception.exp.NotFoundException;
import ru.practicum.shareit.user.repository.UserNameProjection;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.UserEntity;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

public interface UserService {

    UserResponseDto createUser(@Valid UserResponseDto userResponseDto);

    UserResponseDto findUserById(@Positive long userId) throws NotFoundException;

    UserResponseDto updateUser(@Positive long userId,
                               @Valid UserResponseDto userResponseDto) throws NotFoundException;

    void deleteUserById(@Positive long userId) throws NotFoundException;

    List<UserResponseDto> findAllUsers(@PositiveOrZero int from,
                                       @Positive int size);

    UserNameProjection findNameByUserId(@Positive long userId) throws NotFoundException;

    UserEntity findUserEntityById(@Positive long userId) throws NotFoundException;

    void checkUserIsExistById(@Positive long userId) throws NotFoundException;
}
