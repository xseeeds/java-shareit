package ru.practicum.shareit.user.conroller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserResponseDto createUser(@RequestBody UserRequestDto userRequestDto) {
        log.info("SERVER => createUser email => {}", userRequestDto.getEmail());
        return userService.createUser(userRequestDto);
    }

    @GetMapping("/{userId}")
    public UserResponseDto findUserDtoById(@PathVariable long userId) {
        log.info("SERVER => findUserDtoById userId => {}", userId);
        return userService.findUserById(userId);
    }


    @PatchMapping("/{userId}")
    public UserResponseDto updateUser(@PathVariable long userId,
                                      @RequestBody UserRequestDto userRequestDto) {
        log.info("SERVER => updateUser userId => {}", userId);
        return userService.updateUser(userId, userRequestDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable long userId) {
        log.info("SERVER => deleteUserById userId => {}", userId);
        userService.deleteUserById(userId);
    }

    @GetMapping
    public List<UserResponseDto> findAllUsers(@RequestParam(value = "from", defaultValue = "0") int from,
                                              @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("SERVER => findAllUsers");
        return userService.findAllUsers(from, size);
    }

}
