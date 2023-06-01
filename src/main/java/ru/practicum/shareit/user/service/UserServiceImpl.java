package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validation.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUserDto() {
        return userRepository.getAllUser();
    }

    @Override
    public UserDto getUserById(@Positive long userId) {
        return userRepository.getUserById(userId);
    }

    @Validated(Marker.OnCreate.class)
    @Override
    public UserDto createUser(@Valid UserDto userDto) {
        return userRepository.createUser(userDto);
    }

    @Validated(Marker.OnUpdate.class)
    @Override
    public UserDto updateUser(@Positive long userId, @Valid UserDto userDto) {
        return userRepository.updateUser(userId, userDto);
    }

    @Override
    public void deleteUser(@Positive long userId) {
        userRepository.deleteUser(userId);
    }
}
