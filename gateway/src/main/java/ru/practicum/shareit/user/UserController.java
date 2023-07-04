package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.validation.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @Validated(Marker.OnCreate.class)
    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        log.info("GATEWAY => createUser email => {}", userRequestDto.getEmail());
        return userClient.postUser(userRequestDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findUserDtoById(@Positive @PathVariable long userId) {
        log.info("GATEWAY => findUserDtoById userId => {}", userId);
        return userClient.getUserById(userId);
    }

    @Validated(Marker.OnUpdate.class)
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@Positive @PathVariable long userId,
                                             @Valid @RequestBody UserRequestDto userRequestDto) {
        log.info("GATEWAY => updateUser userId => {}", userId);
        return userClient.patchUser(userId, userRequestDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@Positive @PathVariable long userId) {
        log.info("GATEWAY => deleteUserById userId => {}", userId);
        return userClient.deleteUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllUsers(@PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
                                               @Positive @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("GATEWAY => findAllUsers");
        return userClient.getUsers(from, size);
    }

}