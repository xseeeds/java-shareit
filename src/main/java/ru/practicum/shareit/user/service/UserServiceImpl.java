package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoCreate;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.constraints.Positive;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
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

    @Override
    public UserDto createUser(UserDtoCreate userDtoCreate) {
        return userRepository.createUser(userDtoCreate);
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        return userRepository.updateUser(userId, userDto);
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteUser(userId);
    }
}
