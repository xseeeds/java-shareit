package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.expception.exp.NotFoundException;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.UserEntity;

import javax.validation.ConstraintViolationException;

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
        final UserResponseDto user = UserResponseDto
                .builder()
                .name("Name")
                .email("user@ya.ru")
                .build();
        userService.createUser(user);

        final UserEntity userEntity = userService.findUserEntityById(1L);

        assertThat(userEntity.getId(), equalTo(1L));
        assertThrows(NotFoundException.class, () -> userService
                .findUserEntityById(userEntity.getId() + 1));
    }


    @Test
    @Transactional
    void createUserWhenUserIsNotCorrectThenUserNotCreated() {
        final UserResponseDto userResponseDtoNotCorrectName = UserResponseDto
                .builder()
                .name(" ")
                .email("userResponseDtoNotCorrectName@ya.ru").build();
        final UserResponseDto userResponseDtoNotCorrectEmail = UserResponseDto
                .builder()
                .name("Name")
                .email("userResponseDtoNotCorrectEmail_ya.ru").build();

        assertThrows(ConstraintViolationException.class, () -> userService
                .createUser(userResponseDtoNotCorrectName));
        assertThrows(ConstraintViolationException.class, () -> userService
                .createUser(userResponseDtoNotCorrectEmail));
    }

}
