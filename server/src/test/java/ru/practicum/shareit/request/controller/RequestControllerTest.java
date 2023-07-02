package ru.practicum.shareit.request.controller;

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
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.header.HttpHeadersShareIt.X_SHARER_USER_ID;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestControllerTest {

	private final ObjectMapper mapper;
	private final MockMvc mockMvc;

	@MockBean
	private ItemRequestService requestService;

	@Test
	@SneakyThrows
	void createItemRequestWhenRequestIsValidThenItemRequestResponseDtoReturnTest() {

		final ItemRequestRequestDto itemRequestDto = ItemRequestRequestDto
				.builder()
				.description("Item request")
				.build();
		final ItemRequestResponseDto itemRequestResponseDto = ItemRequestResponseDto
				.builder()
				.description("Item request")
				.build();

		when(requestService
				.createItemRequest(anyLong(), any(ItemRequestRequestDto.class)))
				.thenReturn(itemRequestResponseDto);

		mockMvc.perform(post("/requests")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header(X_SHARER_USER_ID, "1")
						.content(mapper.writeValueAsString(itemRequestDto)))
				.andExpectAll(
						status().isOk(),
						jsonPath("$.description", equalTo(itemRequestResponseDto.getDescription())));
	}

	@Test
	@SneakyThrows
	void findItemRequestByIdWhenRequestFoundThenItemRequestResponseDtoReturnTest() {
		final ItemRequestResponseDto itemRequestResponseDto = ItemRequestResponseDto
				.builder()
				.description("Item request")
				.build();

		when(requestService
				.findItemRequestById(anyLong(), anyLong()))
				.thenReturn(itemRequestResponseDto);

		mockMvc.perform(get("/requests/1")
						.accept(MediaType.APPLICATION_JSON)
						.header(X_SHARER_USER_ID, "1"))
				.andExpectAll(
						status().isOk(),
						jsonPath("$.description", equalTo(itemRequestResponseDto.getDescription())));
	}

	@Test
	@SneakyThrows
	void findOwnerItemRequestTest() {
		final ItemRequestResponseDto itemRequestResponseDto = ItemRequestResponseDto
				.builder()
				.description("Item request")
				.build();

		when(requestService
				.findOwnerItemRequest(anyLong(), anyInt(), anyInt()))
				.thenReturn(List.of(itemRequestResponseDto));

		mockMvc.perform(get("/requests")
						.accept(MediaType.APPLICATION_JSON)
						.header(X_SHARER_USER_ID, "1"))
				.andExpectAll(
						status().isOk(),
						jsonPath("$.*", hasSize(1)),
						jsonPath("$..description", hasItems(itemRequestResponseDto.getDescription())));
	}

	@Test
	@SneakyThrows
	void findOthersItemRequestTest() {
		final ItemRequestResponseDto itemRequestResponseDto = ItemRequestResponseDto
				.builder()
				.description("Item request")
				.build();

		when(requestService
				.findOthersItemRequest(anyLong(), anyInt(), anyInt()))
				.thenReturn(List.of(itemRequestResponseDto));

		mockMvc.perform(get("/requests/all")
						.accept(MediaType.APPLICATION_JSON)
						.header(X_SHARER_USER_ID, "1"))
				.andExpectAll(
						status().isOk(),
						jsonPath("$.*", hasSize(1)),
						jsonPath("$..description", hasItems(itemRequestResponseDto.getDescription())));
	}

}