package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;


@Service
public class ItemRequestClient extends BaseClient {

	private static final String API_PREFIX = "/requests";

	@Autowired
	public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
		super(
				builder
						.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
						.requestFactory(HttpComponentsClientHttpRequestFactory::new)
						.build()
		);
	}


	public ResponseEntity<Object> postRequest(long requesterId, ItemRequestRequestDto itemRequestRequestDto) {
		return post("", requesterId, itemRequestRequestDto);
	}

	public ResponseEntity<Object> getRequestById(long requestId, long userId) {
		return get("/" + requestId, userId);
	}

	public ResponseEntity<Object> getOwnRequests(long requesterId, int from, int size) {
		final String path = "?from=" + from + "&size=" + size;
		return get(path, requesterId);
	}

	public ResponseEntity<Object> getOthersRequests(long userId, int from, int size) {
		final String path = "/all" + "?from=" + from + "&size=" + size;
		return get(path, userId);
	}

}