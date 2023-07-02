package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.comment.CommentRequestDto;
import ru.practicum.shareit.item.dto.item.ItemRequestDto;


@Service
public class ItemClient extends BaseClient {

	private static final String API_PREFIX = "/items";

	@Autowired
	public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
		super(
				builder
						.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
						.requestFactory(HttpComponentsClientHttpRequestFactory::new)
						.build()
		);
	}

	public ResponseEntity<Object> postItem(long ownerId, ItemRequestDto itemRequestDto) {
		return post("", ownerId, itemRequestDto);
	}

	public ResponseEntity<Object> getItemById(long userId, long itemId, int from, int size) {
		final String path = "/" + itemId + "?from=" + from + "&size=" + size;
		return get(path, userId);
	}

	public ResponseEntity<Object> patchItem(long userId, long itemId, ItemRequestDto itemRequestDto) {
		return patch("/" + itemId, userId, itemRequestDto);
	}

	public ResponseEntity<Object>  deleteItem(long userId, long itemId) {
		return delete("/" + itemId, userId);
	}

	public ResponseEntity<Object> getItems(long ownerId, int from, int size) {
		final String path = "?from=" + from + "&size=" + size;
		return get(path, ownerId);
	}

	public ResponseEntity<Object> search(String text, int from, int size) {
		final String path = "/search?text=" + text + "&from=" + from + "&size=" + size;
		return get(path);
	}

	public ResponseEntity<Object> addComment(long itemId, long authorId, CommentRequestDto commentRequestDto) {
		return post("/" + itemId + "/comment", authorId, commentRequestDto);
	}

}
