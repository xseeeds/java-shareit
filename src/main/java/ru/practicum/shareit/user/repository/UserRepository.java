package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.expception.exp.ConflictException;
import ru.practicum.shareit.expception.exp.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserRepository {

    List<UserDto> getAllUser();

    UserDto getUserById(long userId);

    UserDto createUser(UserDto userDto) throws ConflictException;

    UserDto updateUser(long userId, UserDto userDto) throws NotFoundException, ConflictException;

    void deleteUser(long userId);

    void checkExistUserById(long userId) throws NotFoundException;

    void checkEmailByExistUserEmails(String email, Long userId) throws ConflictException;
}
