package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.exp.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.repository.UserNameProjection;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.UserEntity;

import java.util.List;

public interface UserService {

    UserResponseDto createUser(UserRequestDto userRequestDto);

    UserResponseDto findUserById(long userId) throws NotFoundException;

    UserResponseDto updateUser(long userId,
                               UserRequestDto userRequestDto) throws NotFoundException;

    void deleteUserById(long userId) throws NotFoundException;

    List<UserResponseDto> findAllUsers(int from,
                                       int size);

    UserNameProjection findNameByUserId(long userId) throws NotFoundException;

    UserEntity findUserEntityById(long userId) throws NotFoundException;

    void checkUserIsExistById(long userId) throws NotFoundException;

}
