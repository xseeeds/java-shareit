package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.exception.exp.BadRequestException;
import ru.practicum.shareit.exception.exp.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.header.HttpHeadersShareIt.X_SHARER_USER_ID;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {

	private final ObjectMapper mapper;
	private final MockMvc mockMvc;

	@MockBean
	private ItemService itemService;


	@Test
	@SneakyThrows
	void createItemWhenItemIsValidThenItemReturnTest() {
		final ItemRequestDto itemRequestDto = ItemRequestDto
				.builder()
				.name("Item")
				.description("Item description")
				.available(true)
				.build();
		final ItemResponseDto itemResponseDto = ItemResponseDto
				.builder()
				.name("Item")
				.description("Item description")
				.available(true)
				.build();

		when(itemService
				.createItem(anyLong(), any(ItemRequestDto.class)))
				.thenReturn(itemResponseDto);

		mockMvc.perform(post("/items")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header(X_SHARER_USER_ID, "1")
						.content(mapper.writeValueAsString(itemRequestDto)))
				.andExpectAll(
						status().isOk(),
						jsonPath("$.name", equalTo("Item")));
	}

	@Test
	@SneakyThrows
	void updateItemWhenUserIsOwnerThenUpdatedItemReturnTest() {
		final ItemResponseDto updatedItemDto = ItemResponseDto.builder()
				.name("updated Item")
				.description("")
				.available(true)
				.requestId(1L)
				.build();

		when(itemService
				.updateItem(anyLong(), anyLong(), any(ItemRequestDto.class)))
				.thenReturn(updatedItemDto);

		mockMvc.perform(patch("/items/1")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header(X_SHARER_USER_ID, "1")
						.content(mapper.writeValueAsString(updatedItemDto)))
				.andExpectAll(
						status().isOk(),
						jsonPath("$.name", equalTo("updated Item")));
	}

	@Test
	@SneakyThrows
	void updateItemWhenUserIsNotOwnerThenNotFoundExceptionTest() {
		final ItemRequestDto updatedItemDto = ItemRequestDto
				.builder()
				.name("updated Item")
				.description("")
				.available(true)
				.requestId(1L)
				.build();

		when(itemService
				.updateItem(anyLong(), anyLong(), any(ItemRequestDto.class)))
				.thenThrow(new NotFoundException("Вещь по id => " + 1L
						+ " не принадлежит пользователю с id => " + 1L));

		mockMvc.perform(patch("/items/1")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header(X_SHARER_USER_ID, "1")
						.content(mapper.writeValueAsString(updatedItemDto)))
				.andExpectAll(
						status().isNotFound());
	}

	@Test
	@SneakyThrows
	void deleteItemWhenItemExistsByIdAndOwnerIdTest() {
		doNothing()
				.when(itemService)
				.deleteItemById(anyLong(), anyLong());

		mockMvc.perform(delete("/items/1")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header(X_SHARER_USER_ID, "1"))
				.andExpectAll(
						status().isOk());
	}

	@Test
	@SneakyThrows
	void deleteItemWhenItemNotExistsByIdAndOwnerIdTest() {
		doThrow(NotFoundException.class)
				.when(itemService)
				.deleteItemById(anyLong(), anyLong());

		mockMvc.perform(delete("/items/1")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header(X_SHARER_USER_ID, "1"))
				.andExpectAll(
						status().isNotFound());
	}

	@Test
	@SneakyThrows
	void findItemWithBookingAndCommentsResponseDtoByIdWhenItemFoundThenItemReturnTest() {
		final CommentResponseDto commentResponseDto = CommentResponseDto
				.builder()
				.id(1L)
				.text("Comment")
				.authorName("Vasya")
				.build();
		final ItemWithBookingAndCommentsResponseDto itemWithBookingAndCommentsResponseDto = ItemWithBookingAndCommentsResponseDto
				.builder()
				.name("Item")
				.description("Item description")
				.available(true)
				.comments(List.of(commentResponseDto))
				.build();

		when(itemService.findItemWithBookingAndCommentsResponseDtoById(
				anyLong(), anyLong(), anyInt(), anyInt()))
				.thenReturn(itemWithBookingAndCommentsResponseDto);

		mockMvc.perform(get("/items/1")
						.accept(MediaType.APPLICATION_JSON)
						.header(X_SHARER_USER_ID, "1"))
				.andExpectAll(
						status().isOk(),
						jsonPath("$.name", equalTo("Item")),
						jsonPath("$.comments.*", hasSize(1)),
						jsonPath("$.comments..authorName", hasItems(commentResponseDto.getAuthorName())));

		verify(itemService, times(1))
				.findItemWithBookingAndCommentsResponseDtoById(
						anyLong(), anyLong(), anyInt(), anyInt());
	}

	@Test
	@SneakyThrows
	void findListItemWithBookingAndCommentsResponseDtoByOwnerIdTest() {
		final ItemWithBookingAndCommentsResponseDto itemWithBookingAndCommentsResponseDto1 = ItemWithBookingAndCommentsResponseDto
				.builder()
				.name("Item 1")
				.description("Item 1 description")
				.available(true)
				.build();
		final ItemWithBookingAndCommentsResponseDto itemWithBookingAndCommentsResponseDto2 = ItemWithBookingAndCommentsResponseDto
				.builder()
				.name("Item 2")
				.description("Item 2 description")
				.available(true)
				.build();

		when(itemService
				.findItemWithBookingAndCommentsResponseDtoByOwnerId(anyLong(), anyInt(), anyInt()))
				.thenReturn(List.of(itemWithBookingAndCommentsResponseDto1, itemWithBookingAndCommentsResponseDto2));

		mockMvc.perform(get("/items")
						.accept(MediaType.APPLICATION_JSON)
						.header(X_SHARER_USER_ID, "1"))
				.andExpectAll(
						status().isOk(),
						jsonPath("$", hasSize(2)),
						jsonPath("$..name",
								hasItems(itemWithBookingAndCommentsResponseDto1.getName(),
										itemWithBookingAndCommentsResponseDto2.getName())));
	}

	@Test
	@SneakyThrows
	void searchItems_whenTextNotBlank_thenListReturned() {
		final ItemResponseDto itemResponseDto = ItemResponseDto.builder()
				.name("Item (something)")
				.description("Item description")
				.available(true)
				.build();

		when(itemService
				.findItemIsAvailableByNameOrDescription(anyString(), anyInt(), anyInt()))
				.thenReturn(singletonList(itemResponseDto));

		mockMvc.perform(get("/items/search")
						.accept(MediaType.APPLICATION_JSON)
						.param("text", "search text"))
				.andExpectAll(
						status().isOk(),
						jsonPath("$.*", hasSize(1)),
						jsonPath("$..name", hasItems(itemResponseDto.getName())));
	}

	@Test
	@SneakyThrows
	void findItemIsAvailableByNameOrDescriptionWhenTextBlankThenEmptyListReturnTest() {
		when(itemService
				.findItemIsAvailableByNameOrDescription(anyString(), anyInt(), anyInt()))
				.thenReturn(emptyList());

		mockMvc.perform(get("/items/search")
						.accept(MediaType.APPLICATION_JSON)
						.param("text", ""))
				.andExpectAll(
						status().isOk(),
						jsonPath("$.*", hasSize(0)));
	}

	@Test
	@SneakyThrows
	void createCommentWhenExistsBookingByItemIdAndBookerIdAndEndIsBeforeNowAndStatusApprovedThenCommentReturnTest() {
		final CommentRequestDto commentRequestDto = CommentRequestDto
				.builder()
				.text("new comment")
				.build();
		final CommentResponseDto commentResponseDto = CommentResponseDto
				.builder()
				.text("new comment")
				.authorName("Author")
				.created(LocalDateTime.now())
				.build();

		when(itemService
				.createComment(anyLong(), anyLong(), any(CommentRequestDto.class)))
				.thenReturn(commentResponseDto);

		mockMvc.perform(post("/items/1/comment")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.param("itemId", "1")
						.header(X_SHARER_USER_ID, "1")
						.content(mapper.writeValueAsString(commentRequestDto)))
				.andExpectAll(
						status().isOk(),
						jsonPath("$.text", equalTo("new comment")),
						jsonPath("$.authorName", equalTo("Author"))
				);

	}

	@Test
	@SneakyThrows
	void createCommentWhenNotExistsBookingByItemIdAndBookerIdAndEndIsBeforeNowAndStatusApprovedThenBadRequestExceptionTest() {
		final CommentRequestDto commentRequestDto = CommentRequestDto
				.builder()
				.text("new comment")
				.build();

		doThrow(BadRequestException.class)
				.when(itemService)
				.createComment(anyLong(), anyLong(), any(CommentRequestDto.class));

		mockMvc.perform(post("/items/1/comment")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.param("itemId", "1")
						.header(X_SHARER_USER_ID, "1")
						.content(mapper.writeValueAsString(commentRequestDto)))
				.andExpectAll(
						status().isBadRequest());
	}

}