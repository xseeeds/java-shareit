package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.expception.exp.NotFoundException;
import ru.practicum.shareit.user.conroller.UserController;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {

	private final ObjectMapper mapper;
	private final MockMvc mockMvc;

	@MockBean
	UserService userService;

	@Test
	@SneakyThrows
	void createUserWhenCorrectUserThenUserPosted() {
		final UserResponseDto requestDto = UserResponseDto
				.builder()
				.name("User")
				.email("user@ya.ru")
				.build();
		when(userService.createUser(any(UserResponseDto.class)))
				.thenReturn(requestDto);

		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(requestDto)))
				.andExpectAll(
						status().isCreated(),
						jsonPath("$.name", equalTo(requestDto.getName())),
						jsonPath("$.email", equalTo(requestDto.getEmail())));

	}

	@Test
	@SneakyThrows
	void updateUserWhenUserUpdateThenUserUpdatedReturnTest() {
		final UserResponseDto requestDto = UserResponseDto
				.builder()
				.name("updated User")
				.email("updated@ya.ru")
				.build();
		final UserResponseDto updatedUserDto = UserResponseDto
				.builder()
				.name("updated User")
				.email("updated@ya.ru")
				.build();

		when(userService.updateUser(anyLong(), any(UserResponseDto.class)))
				.thenReturn(updatedUserDto);

		mockMvc.perform(patch("/users/1")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(requestDto)))
				.andExpectAll(
						status().isOk(),
						jsonPath("$.name", equalTo(updatedUserDto.getName())),
						jsonPath("$.email", equalTo(updatedUserDto.getEmail())));
	}

	@Test
	@SneakyThrows
	void deleteUserByIdTest() {
		mockMvc.perform(delete("/users/1")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(
						status().isOk());

		verify(userService, times(1))
				.deleteUserById(anyLong());
	}

	@Test
	@SneakyThrows
	void findUserByIdWhenUserFoundThenUserResponseDtoReturnTest() {
		final UserResponseDto responseDto = UserResponseDto
				.builder()
				.name("User A")
				.email("user@ya.ru")
				.build();

		when(userService
				.findUserById(anyLong()))
				.thenReturn(responseDto);

		mockMvc.perform(get("/users/1")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(
						status().isOk());

		verify(userService, times(1))
				.findUserById(anyLong());
	}

	@Test
	@SneakyThrows
	void findUserByIdWhenUserNotFoundThenNotFoundExceptionTest() {
		doThrow(NotFoundException.class)
				.when(userService)
				.findUserById(anyLong());

		mockMvc.perform(get("/users/1")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status()
						.isNotFound());

		verify(userService, times(1))
				.findUserById(anyLong());
	}

	@Test
	@SneakyThrows
	void findAllUsersThenUserResponseDtoListReturnTest() {
		final UserResponseDto userResponseDto1 = UserResponseDto
				.builder()
				.name("User 1")
				.email("user_1@ya.ru")
				.build();
		final UserResponseDto userResponseDto2 = UserResponseDto
				.builder()
				.name("User 2")
				.email("user_2@ya.ru")
				.build();

		when(userService
				.findAllUsers(anyInt(), anyInt()))
				.thenReturn(List.of(userResponseDto1, userResponseDto2));

		mockMvc.perform(get("/users")
						.accept(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isOk(),
						jsonPath("$", hasSize(2)),
						jsonPath("$..name",
								hasItems(userResponseDto1.getName(), userResponseDto2.getName())));

		verify(userService, times(1))
				.findAllUsers(anyInt(), anyInt());
	}

}