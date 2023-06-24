package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.expception.exp.NotFoundException;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.UserEntity;
import ru.practicum.shareit.user.repository.UserNameProjection;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private UserResponseDto userResponseDto;
    private UserEntity userEntity;
    private Page<UserEntity> userEntityPage;

    @BeforeEach
    void setUp() {
        userResponseDto = UserResponseDto
                .builder()
                .name("Name")
                .email("UserResponseDto@ya.ru")
                .build();
        userEntity = UserEntity
                .builder()
                .name("Name")
                .email("UserEntity@ya.ru")
                .build();
        userEntityPage = new PageImpl<>(singletonList(userEntity));
    }

    @Test
    void createUserWhenUserIsCorrectThenUserAdded() {
        when(userRepository
                .save(any(UserEntity.class)))
                .thenReturn(userEntity);

        final UserResponseDto addedUser = userService.createUser(userResponseDto);

        assertThat(addedUser.getName(), equalTo(userEntity.getName()));
        assertThat(addedUser.getEmail(), equalTo(userEntity.getEmail()));

        verify(userRepository, times(1))
                .save(any(UserEntity.class));
    }

    @Test
    void findUserByIdWhenUserFoundThenUserReturnTest() {
        when(userRepository
                .findById(anyLong()))
                .thenReturn(Optional.of(userEntity));

        final UserResponseDto userResponseDto = userService.findUserById(1L);

        assertThat(userResponseDto.getName(), equalTo(userEntity.getName()));
        assertThat(userResponseDto.getEmail(), equalTo(userEntity.getEmail()));

        verify(userRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void findUserByIdWhenUserNotFoundThenNotFoundExceptionTest() {
        doThrow(NotFoundException.class)
                .when(userRepository)
                .findById(anyLong());

        assertThrows(NotFoundException.class, () -> userService
                .findUserById(1L));

        verify(userRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void updateUserWhenUserIsCorrectThenUserResponseDtoReturnTest() {
        when(userRepository
                .findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(userRepository
                .save(any(UserEntity.class)))
                .then(returnsFirstArg());

        final UserResponseDto updatedUser = userService.updateUser(1L, userResponseDto);

        assertThat(updatedUser.getName(), equalTo(userResponseDto.getName()));
        assertThat(updatedUser.getEmail(), equalTo(userResponseDto.getEmail()));

        verify(userRepository, times(1))
                .findById(anyLong());
        verify(userRepository, times(1))
                .save(any(UserEntity.class));
    }

    @Test
    void deleteUserByIdWhenUserFoundThenUserDeletedTest() {
        userService.deleteUserById(1L);

        verify(userRepository, times(1))
                .deleteById(anyLong());
    }

    @Test
    void deleteUserByIdWhenUserNotFoundThenNotFoundExceptionTest() {
        doThrow(EmptyResultDataAccessException.class)
                .when(userRepository)
                .deleteById(anyLong());

        assertThrows(NotFoundException.class, () -> userService
                .deleteUserById(1L));

        verify(userRepository, times(1))
                .deleteById(anyLong());
    }


    @Test
    void findAllUsersTest() {
        when(userRepository
                .findAll(isA(Pageable.class)))
                .thenReturn(userEntityPage);

        userService.findAllUsers(0, 10);

        verify(userRepository, times(1))
                .findAll(isA(Pageable.class));
    }

    @Test
    void findNameByUserId() {
        when(userRepository
                .findNameById(anyLong()))
                .thenReturn(Optional.of(() -> "Пётр"));

        final UserNameProjection userNameProjection = userService.findNameByUserId(1L);

        assertThat(userNameProjection.getName(), equalTo( "Пётр"));

        verify(userRepository, times(1))
                .findNameById(anyLong());
    }

    @Test
    void checkUserIsExistByIdWhenUserFoundTest() {
        when(userRepository
                .existsById(anyLong()))
                .thenReturn(true);

        userService.checkUserIsExistById(1L);

        verify(userRepository, times(1))
                .existsById(anyLong());
    }

    @Test
    void checkUserIsExistByIdWhenUserNotFoundTest() {
        when(userRepository
                .existsById(anyLong()))
                .thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService
                .checkUserIsExistById(1L));

        verify(userRepository, times(1))
                .existsById(anyLong());
    }

}