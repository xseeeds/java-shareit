package ru.practicum.shareit.user.conroller;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@RequestBody UserResponseDto userResponseDto) {
        log.info("createUser email => {}", userResponseDto.getEmail());
        return userService.createUser(userResponseDto);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto findUserDtoById(@PathVariable long userId) {
        log.info("findUserDtoById userId => {}", userId);
        return userService.findUserById(userId);
    }


    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto updateUser(@PathVariable long userId,
                                      @RequestBody UserResponseDto userResponseDto) {
        log.info("updateUser userId => {}", userId);
        return userService.updateUser(userId, userResponseDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserById(@PathVariable long userId) {
        log.info("deleteUserById userId => {}", userId);
        userService.deleteUserById(userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDto> findAllUsers(@RequestParam(value = "from", defaultValue = "0") int from,
                                              @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("findAllUsers");
        return userService.findAllUsers(from, size);
    }

}
