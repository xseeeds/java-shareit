package ru.practicum.shareit.request;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.header.HttpHeadersShareIt.X_SHARER_USER_ID;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestControllerTest {

	private final ObjectMapper mapper;
	private final MockMvc mockMvc;

	@MockBean
	private ItemRequestClient itemRequestClient;

	@Test
	@SneakyThrows
	void findOthersRequestsWhenIncorrectPagingThenStatusIsBadRequest() {
		mockMvc.perform(get("/requests/all")
						.accept(MediaType.APPLICATION_JSON)
						.param("from", "-100")
						.param("size", "10")
						.header(X_SHARER_USER_ID, "1"))
				.andExpectAll(
						status().isBadRequest());
	}

}
