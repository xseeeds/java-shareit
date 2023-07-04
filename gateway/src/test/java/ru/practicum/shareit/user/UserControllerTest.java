package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserRequestDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {

	private final ObjectMapper mapper;
	private final MockMvc mockMvc;

	@MockBean
    private UserClient userClient;

	@Test
	@SneakyThrows
	void createUserWhenIncorrectNameOrEmailThenStatusIsBadRequest() {
		final UserRequestDto userRequestDtoNotCorrectName = UserRequestDto
				.builder()
				.name(" ")
				.email("userRequestDtoNotCorrectName@ya.ru")
				.build();
		final UserRequestDto userRequestDtoNotCorrectEmail = UserRequestDto
				.builder()
				.name("Name")
				.email("userRequestDtoNotCorrectEmail_ya.ru")
				.build();

		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(userRequestDtoNotCorrectName)))
				.andExpect(
						status().isBadRequest());

		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(userRequestDtoNotCorrectEmail)))
				.andExpect(
						status().isBadRequest());
	}

	@Test
	@SneakyThrows
	void updateUserWhenIncorrectNameOrEmailThenStatusIsBadRequest() {

		final UserRequestDto userRequestDtoNotCorrectName = UserRequestDto
				.builder()
				.name(" ")
				.email("userRequestDtoNotCorrectName@ya.ru")
				.build();
		final UserRequestDto userRequestDtoNotCorrectEmail = UserRequestDto
				.builder()
				.name("Name")
				.email("userRequestDtoNotCorrectEmail_ya.ru")
				.build();

		mockMvc.perform(patch("/users/1")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(userRequestDtoNotCorrectName)))
				.andExpect(
						status().isBadRequest());

		mockMvc.perform(patch("/users/1")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(userRequestDtoNotCorrectEmail)))
				.andExpect(
						status().isBadRequest());
	}

}
