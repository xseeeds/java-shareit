package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.expception.exp.NotFoundException;
import ru.practicum.shareit.user.repository.UserNameProjection;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.UserEntity;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.Util;


import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Override
    public UserResponseDto createUser(UserResponseDto userResponseDto) {
        final UserResponseDto savedUser = UserMapper.toUserResponseDto(
                userRepository.save(
                        UserMapper.toUserEntity(userResponseDto)));
        log.info("Пользователь c id => {} сохранен", savedUser.getId());
        return savedUser;
    }

    @Override
    public UserResponseDto findUserById(long userId) throws NotFoundException {
        final UserResponseDto foundUser = UserMapper.toUserResponseDto(
                userRepository.findById(userId)
                        .orElseThrow(
                                () -> new NotFoundException("Пользователь по id => " + userId
                                        + " не существует")));
        log.info("Пользователь по id => {} получен", userId);
        return foundUser;
    }

    @Override
    public UserResponseDto updateUser(long userId, UserResponseDto userResponseDto) throws NotFoundException {
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

    @Override
    public void deleteUserById(long userId) throws NotFoundException {
        try {
            userRepository.deleteById(userId);
            log.info("Пользователь по id => {} удален", userId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь по id => " + userId + " не существует");
        }
    }

    @Override
    public List<UserResponseDto> findAllUsers(int from,
                                              int size) {
        final Page<UserResponseDto> userResponseDtoPage = userRepository
                .findAll(Util.getPageSortAscByProperties(from, size, "id"))
                .map(UserMapper::toUserResponseDto);
        log.info("Пользователи получены size => {}", userResponseDtoPage.getTotalElements());
        return userResponseDtoPage.getContent();
    }

    @Override
    public UserNameProjection findNameByUserId(long userId) throws NotFoundException {
        log.info("Получено имя пользователя по id => {} для СЕРВИСОВ", userId);
        return userRepository.findNameById(userId)
                .orElseThrow(
                        () -> new NotFoundException("Пользователь по id => "
                                + userId + " не существует поиск СЕРВИСОВ"));
    }

    @Override
    public UserEntity findUserEntityById(long userId) throws NotFoundException {
        log.info("Пользователь по id => {} получен для СЕРВИСОВ", userId);
        return userRepository.findById(userId)
                .orElseThrow(
                        () -> new NotFoundException("Пользователь по id => "
                                + userId + " не существует поиск СЕРВИСОВ"));
    }

    @Override
    public void checkUserIsExistById(long userId) throws NotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь по id => " + userId + " не существует");
        }
    }

}
