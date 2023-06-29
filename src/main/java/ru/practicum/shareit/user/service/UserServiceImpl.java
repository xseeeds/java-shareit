package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.expception.exp.NotFoundException;
import ru.practicum.shareit.user.repository.UserNameProjection;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.UserEntity;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.Util;
import ru.practicum.shareit.validation.Marker;


import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Service
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Validated(Marker.OnCreate.class)
    @Modifying
    @Override
    public UserResponseDto createUser(@Valid UserResponseDto userResponseDto) {
        final UserResponseDto savedUser = UserMapper.toUserResponseDto(
                userRepository.save(
                        UserMapper.toUserEntity(userResponseDto)));
        log.info("Пользователь c id => {} сохранен", savedUser.getId());
        return savedUser;
    }

    @Override
    public UserResponseDto findUserById(@Positive long userId) throws NotFoundException {
        final UserResponseDto foundUser = UserMapper.toUserResponseDto(
                userRepository.findById(userId)
                        .orElseThrow(
                                () -> new NotFoundException("Пользователь по id => " + userId
                                        + " не существует")));
        log.info("Пользователь по id => {} получен", userId);
        return foundUser;
    }

    @Transactional
    @Validated(Marker.OnUpdate.class)
    @Modifying
    @Override
    public UserResponseDto updateUser(@Positive long userId,
                                      @Valid UserResponseDto userResponseDto) throws NotFoundException {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь по id => " + userId
                        + " не существует"));

        userEntity = userEntity
                .toBuilder()
                .email(userResponseDto.getEmail() != null ? userResponseDto.getEmail() : userEntity.getEmail())
                .name(userResponseDto.getName() != null ? userResponseDto.getName() : userEntity.getName())
                .build();

        final UserResponseDto updatedUser = UserMapper.toUserResponseDto(userRepository.save(userEntity));

        log.info("Пользователь по id => {} обновлен", userId);
        return updatedUser;
    }

    @Transactional
    @Modifying
    @Override
    public void deleteUserById(@Positive long userId) throws NotFoundException {
        try {
            userRepository.deleteById(userId);
            log.info("Пользователь по id => {} удален", userId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь по id => " + userId + " не существует");
        }
    }

    @Override
    public List<UserResponseDto> findAllUsers(@PositiveOrZero int from,
                                              @Positive int size) {
        final Page<UserResponseDto> userResponseDtoPage = userRepository
                .findAll(Util.getPageSortAscByProperties(from, size, "id"))
                .map(UserMapper::toUserResponseDto);
        log.info("Пользователи получены size => {}", userResponseDtoPage.getTotalElements());
        return userResponseDtoPage.getContent();
    }

    @Override
    public UserNameProjection findNameByUserId(@Positive long userId) throws NotFoundException {
        log.info("Получено имя пользователя по id => {} для СЕРВИСОВ", userId);
        return userRepository.findNameById(userId)
                .orElseThrow(
                        () -> new NotFoundException("Пользователь по id => "
                                + userId + " не существует поиск СЕРВИСОВ"));
    }

    @Override
    public UserEntity findUserEntityById(@Positive long userId) throws NotFoundException {
        log.info("Пользователь по id => {} получен для СЕРВИСОВ", userId);
        return userRepository.findById(userId)
                .orElseThrow(
                        () -> new NotFoundException("Пользователь по id => "
                                + userId + " не существует поиск СЕРВИСОВ"));
    }

    @Override
    public void checkUserIsExistById(@Positive long userId) throws NotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь по id => " + userId + " не существует");
        }
    }

}
