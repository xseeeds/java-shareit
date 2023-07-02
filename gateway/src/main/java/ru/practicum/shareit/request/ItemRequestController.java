package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.header.HttpHeadersShareIt.X_SHARER_USER_ID;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @Positive @RequestHeader(X_SHARER_USER_ID) long requesterId,
            @Valid @RequestBody ItemRequestRequestDto itemRequestRequestDto) {
        log.info("GETAWAY => createItemRequest requesterId => {}", requesterId);
        return itemRequestClient.postRequest(requesterId, itemRequestRequestDto);
    }

    @GetMapping("/{itemRequestId}")
    public ResponseEntity<Object> findItemRequestById(
            @Positive @RequestHeader(X_SHARER_USER_ID) long userId,
            @Positive @PathVariable long itemRequestId) {
        log.info("GETAWAY => findItemRequestById userId => {}, itemRequestId => {}", userId, itemRequestId);
        return itemRequestClient.getRequestById(itemRequestId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findOwnerItemRequest(
            @Positive @RequestHeader(X_SHARER_USER_ID) long requesterId,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
            @Positive @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("GETAWAY => findOwnerItemRequest requesterId => {}", requesterId);
        return itemRequestClient.getOwnRequests(requesterId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findOthersItemRequest(
            @Positive @RequestHeader(X_SHARER_USER_ID) long userId,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
            @Positive @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("GETAWAY => findOthersItemRequest userId => {}", userId);
        return itemRequestClient.getOthersRequests(userId, from, size);
    }

}