package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.header.HttpHeadersShareIt.X_SHARER_USER_ID;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemResponseDto createItem(@RequestHeader(X_SHARER_USER_ID) long ownerId,
                                      @RequestBody ItemRequestDto itemRequestDto) {
        log.info("SERVER => createItem ownerId => {}", ownerId);
        return itemService.createItem(ownerId, itemRequestDto);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingAndCommentsResponseDto findItemWithBookingAndCommentsResponseDtoById(
            @RequestHeader(X_SHARER_USER_ID) long userId,
            @PathVariable long itemId,
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("SERVER => findItemWithBookingAndCommentsResponseDtoById userId => {}, itemId => {}", userId, itemId);
        return itemService.findItemWithBookingAndCommentsResponseDtoById(userId, itemId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(@RequestHeader(X_SHARER_USER_ID) long ownerId,
                                      @PathVariable long itemId,
                                      @RequestBody ItemRequestDto itemRequestDto) {
        log.info("SERVER => updateItem ownerId => {}, itemId => {}", ownerId, itemId);
        return itemService.updateItem(ownerId, itemId, itemRequestDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@RequestHeader(X_SHARER_USER_ID) long ownerId,
                          @PathVariable long itemId) {
        log.info("SERVER => deleteItemById ownerId => {}, itemId => {}", ownerId, itemId);
        itemService.deleteItemById(ownerId, itemId);
    }

    @GetMapping
    public List<ItemWithBookingAndCommentsResponseDto> findItemsWithBookingResponseDtoByOwnerId(
            @RequestHeader(X_SHARER_USER_ID) long ownerId,
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("SERVER => findItemsWithBookingResponseDtoByOwnerId ownerId => {}", ownerId);
        return itemService.findItemWithBookingAndCommentsResponseDtoByOwnerId(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> findItemIsAvailableByNameOrDescription(
            @RequestParam(value = "text") String text,
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("SERVER => findItemIsAvailableByNameOrDescription text => {}", text);
        return itemService.findItemIsAvailableByNameOrDescription(text, from, size);
    }


    @PostMapping("/{itemId}/comment")
    @Validated
    public CommentResponseDto createComment(@RequestHeader(X_SHARER_USER_ID) long bookerId,
                                            @PathVariable long itemId,
                                            @RequestBody CommentRequestDto commentRequestDto) {
        log.info("SERVER => createComment bookerId => {}, itemId => {}", bookerId, itemId);
        return itemService.createComment(bookerId, itemId, commentRequestDto);
    }


}
