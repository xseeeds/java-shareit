package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.exp.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.UserEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {

    private final UserService userService;


    @Test
    @Transactional
    void findUserEntityByIdWhenUserPresentThenUserReturnTest() {
        final UserRequestDto user = UserRequestDto
                .builder()
                .name("Name")
                .email("user@ya.ru")
                .build();

        final UserResponseDto userResponseDto = userService.createUser(user);

        final UserEntity userEntity = userService.findUserEntityById(userResponseDto.getId());

        assertThat(userEntity.getId(), equalTo(userResponseDto.getId()));
        assertThrows(NotFoundException.class, () -> userService
                .findUserEntityById(userEntity.getId() + 1));
    }

/*
    @Test
    @Transactional
    void createUserWhenUserIsNotCorrectThenUserNotCreated() {
        final UserRequestDto userResponseDtoNotCorrectName = UserRequestDto
                .builder()
                .name(" ")
                .email("userResponseDtoNotCorrectName@ya.ru")
                .build();
        final UserRequestDto userResponseDtoNotCorrectEmail = UserRequestDto
                .builder()
                .name("Name")
                .email("userResponseDtoNotCorrectEmail_ya.ru")
                .build();

        assertThrows(ConstraintViolationException.class, () -> userService
                .createUser(userResponseDtoNotCorrectName));
        assertThrows(ConstraintViolationException.class, () -> userService
                .createUser(userResponseDtoNotCorrectEmail));
    }
*/

}
