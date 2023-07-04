package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.exp.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.repository.UserNameProjection;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.UserEntity;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.Util;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Modifying
    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        final UserResponseDto savedUser = UserMapper.toUserResponseDto(
                userRepository.save(
                        UserMapper.toUserEntity(userRequestDto)));
        log.info("SERVICE => Пользователь c id => {} сохранен", savedUser.getId());
        return savedUser;
    }

    @Override
    public UserResponseDto findUserById(long userId) throws NotFoundException {
        final UserResponseDto foundUser = UserMapper.toUserResponseDto(
                userRepository.findById(userId)
                        .orElseThrow(
                                () -> new NotFoundException("SERVICE => Пользователь по id => " + userId
                                        + " не существует")));
        log.info("SERVICE => Пользователь по id => {} получен", userId);
        return foundUser;
    }

    @Transactional
    @Modifying
    @Override
    public UserResponseDto updateUser(long userId,
                                      UserRequestDto userRequestDto) throws NotFoundException {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("SERVICE => Пользователь по id => " + userId
                        + " не существует"));

        userEntity = userEntity
                .toBuilder()
                .email(userRequestDto.getEmail() != null ? userRequestDto.getEmail() : userEntity.getEmail())
                .name(userRequestDto.getName() != null ? userRequestDto.getName() : userEntity.getName())
                .build();

        final UserResponseDto updatedUser = UserMapper.toUserResponseDto(userRepository.save(userEntity));

        log.info("SERVICE => Пользователь по id => {} обновлен", userId);
        return updatedUser;
    }

    @Transactional
    @Modifying
    @Override
    public void deleteUserById(long userId) throws NotFoundException {
        try {
            userRepository.deleteById(userId);
            log.info("SERVICE => Пользователь по id => {} удален", userId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("SERVICE => Пользователь по id => " + userId + " не существует");
        }
    }

    @Override
    public List<UserResponseDto> findAllUsers(int from,
                                              int size) {
        final Page<UserResponseDto> userResponseDtoPage = userRepository
                .findAll(Util.getPageSortAscByProperties(from, size, "id"))
                .map(UserMapper::toUserResponseDto);
        log.info("SERVICE => Пользователи получены size => {}", userResponseDtoPage.getTotalElements());
        return userResponseDtoPage.getContent();
    }

    @Override
    public UserNameProjection findNameByUserId(long userId) throws NotFoundException {
        log.info("SERVICE => Запрос имени пользователя по id => {} для СЕРВИСОВ", userId);
        return userRepository.findNameById(userId)
                .orElseThrow(
                        () -> new NotFoundException("SERVICE => Пользователь по id => "
                                + userId + " не существует поиск СЕРВИСОВ"));
    }

    @Override
    public UserEntity findUserEntityById(long userId) throws NotFoundException {
        log.info("SERVICE => Запрос пользователь по id => {} получен для СЕРВИСОВ", userId);
        return userRepository.findById(userId)
                .orElseThrow(
                        () -> new NotFoundException("SERVICE => Пользователь по id => "
                                + userId + " не существует поиск СЕРВИСОВ"));
    }

    @Override
    public void checkUserIsExistById(long userId) throws NotFoundException {
        log.info("SERVICE => Запрос существует пользователь по id => {} для СЕРВИСОВ", userId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("SERVICE => Пользователь по id => " + userId + " не существует");
        }
    }

}
