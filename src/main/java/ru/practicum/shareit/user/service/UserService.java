package ru.practicum.shareit.user.service;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.expception.exp.NotFoundException;
import ru.practicum.shareit.user.repository.UserNameProjection;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.UserEntity;
import ru.practicum.shareit.validation.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Service
@Validated
@Transactional(readOnly = true)
public interface UserService {

    @Transactional
    @Validated(Marker.OnCreate.class)
    @Modifying
    UserResponseDto createUser(@Valid UserResponseDto userResponseDto);

    UserResponseDto findUserById(@Positive long userId) throws NotFoundException;

    @Transactional
    @Validated(Marker.OnUpdate.class)
    @Modifying
    UserResponseDto updateUser(@Positive long userId,
                               @Valid UserResponseDto userResponseDto) throws NotFoundException;

    @Transactional
    @Modifying
    void deleteUserById(@Positive long userId) throws NotFoundException;

    List<UserResponseDto> findAllUsers(@PositiveOrZero int from,
                                       @Positive int size);

    UserNameProjection findNameByUserId(long userId) throws NotFoundException;

    UserEntity findUserEntityById(@Positive long userId) throws NotFoundException;

    void checkUserIsExistById(@Positive long userId) throws NotFoundException;
}
