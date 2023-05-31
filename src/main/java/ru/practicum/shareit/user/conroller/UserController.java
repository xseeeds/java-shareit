package ru.practicum.shareit.user.conroller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUser() {
        return userService.getAllUserDto();
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserDtoById(@PathVariable long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")          //?name={login}&email={email}
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@PathVariable long userId, @RequestBody UserDto userDto) {
            return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);
    }
}
