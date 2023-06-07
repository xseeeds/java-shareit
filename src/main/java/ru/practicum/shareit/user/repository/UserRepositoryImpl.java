package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.expception.exp.ConflictException;
import ru.practicum.shareit.expception.exp.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.UserEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;
    private final Map<Long, UserEntity> users = new HashMap<>();
    private long generatorId = 0;

    @Override
    public List<UserDto> getAllUser() {

        return users
                .values()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(long userId) {

        checkExistUserById(userId);

        return userMapper.toUserDto(users.get(userId));
    }

    @Override
    public UserDto createUser(UserDto userDto) throws ConflictException {

        checkEmailByExistUserEmails(userDto.getEmail(), null);

        final UserEntity createdUserEntity = userMapper
                .toUser(userDto)
                .toBuilder()
                .id(getNextGeneratorId())
                .build();

        users.put(createdUserEntity.getId(), createdUserEntity);

        return userMapper.toUserDto(createdUserEntity);
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) throws NotFoundException, ConflictException {

        checkExistUserById(userId);

        checkEmailByExistUserEmails(userDto.getEmail(), userId);

        UserEntity existUserEntity = users.get(userId);

        if (userDto.getEmail() != null) {
            existUserEntity = existUserEntity.toBuilder().email(userDto.getEmail()).build();

        }
        if (userDto.getName() != null) {
            existUserEntity = existUserEntity.toBuilder().name(userDto.getName()).build();
        }

        users
                .put(userId,
                        existUserEntity);

        return userMapper.toUserDto(existUserEntity);
    }

    @Override
    public void deleteUser(long userId) {

        checkExistUserById(userId);

        users.remove(userId);
    }

    @Override
    public void checkExistUserById(long userId) throws NotFoundException {

        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь по id => " + userId + " не найден");
        }
    }

    @Override
    public void checkEmailByExistUserEmails(String email, Long userId) throws ConflictException {

        if (users
                .values()
                .stream()
                .anyMatch(user -> user.getEmail().equals(email) && !user.getId().equals(userId))) {
            throw new ConflictException("Пользователь с email => " + email + " уже существует");
        }
    }

    private long getNextGeneratorId() {
        return ++generatorId;
    }

}
