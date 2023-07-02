package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.comment.CommentRequestDto;
import ru.practicum.shareit.item.dto.item.ItemRequestDto;
import ru.practicum.shareit.validation.Marker;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;
import java.util.Optional;

import static ru.practicum.shareit.header.HttpHeadersShareIt.X_SHARER_USER_ID;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @Validated(Marker.OnCreate.class)
    @PostMapping
    public ResponseEntity<Object> createItem(@Positive @RequestHeader(X_SHARER_USER_ID) long ownerId,
                                             @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("GETAWAY => createItem ownerId => {}", ownerId);
        return itemClient.postItem(ownerId, itemRequestDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemWithBookingAndCommentsResponseDtoById(
            @Positive @RequestHeader(X_SHARER_USER_ID) long userId,
            @Positive @PathVariable long itemId,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
            @Positive @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("GETAWAY => findItemWithBookingAndCommentsResponseDtoById userId => {}, itemId => {}", userId, itemId);
        return itemClient.getItemById(userId, itemId, from, size);
    }

    @Validated(Marker.OnUpdate.class)
    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Positive @RequestHeader(X_SHARER_USER_ID) long ownerId,
                                             @Positive @PathVariable long itemId,
                                             @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("GETAWAY => updateItem ownerId => {}, itemId => {}", ownerId, itemId);
        return itemClient.patchItem(ownerId, itemId, itemRequestDto);
    }


    @DeleteMapping("/{itemId}")
    public void deleteItemById(@Positive @RequestHeader(X_SHARER_USER_ID) long ownerId,
                               @Positive @PathVariable long itemId) {
        log.info("GETAWAY => deleteItemById ownerId => {}, itemId => {}", ownerId, itemId);
        itemClient.deleteItem(ownerId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findItemsWithBookingResponseDtoByOwnerId(
            @Positive @RequestHeader(X_SHARER_USER_ID) long ownerId,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
            @Positive @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("GETAWAY => findItemsWithBookingResponseDtoByOwnerId ownerId => {}", ownerId);
        return itemClient.getItems(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemIsAvailableByNameOrDescription(
            @NotNull @RequestParam(value = "text") String text,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
            @Positive @RequestParam(value = "size", defaultValue = "10") int size) {

        if (text.isBlank()) {
            log.info("GETAWAY => Получен пустой запрос поиска вещи");
            return ResponseEntity.of(Optional.of(Collections.emptyList()));
        }
        log.info("GETAWAY => findItemIsAvailableByNameOrDescription text => {}", text);
        return itemClient.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Positive @RequestHeader(X_SHARER_USER_ID) long bookerId,
                                                @Positive @PathVariable long itemId,
                                                @Valid @RequestBody CommentRequestDto commentRequestDto) {
        log.info("GETAWAY => createComment bookerId => {}, itemId => {}", bookerId, itemId);
        return itemClient.addComment(itemId, bookerId, commentRequestDto);
    }

}
