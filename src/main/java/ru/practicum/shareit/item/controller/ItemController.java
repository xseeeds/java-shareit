package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.header.HttpHeadersShareIt;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemResponseDto createItem(@RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long ownerId,
                                      @RequestBody ItemResponseDto itemResponseDto) {
        log.info("createItem ownerId => {}", ownerId);
        return itemService.createItem(ownerId, itemResponseDto);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingAndCommentsResponseDto findItemWithBookingAndCommentsResponseDtoById(
            @RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long userId,
            @PathVariable long itemId,
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("findItemWithBookingAndCommentsResponseDtoById userId => {}, itemId => {}", userId, itemId);
        return itemService.findItemWithBookingAndCommentsResponseDtoById(userId, itemId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(@RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long ownerId,
                                      @PathVariable long itemId,
                                      @RequestBody ItemResponseDto itemResponseDto) {
        log.info("updateItem ownerId => {}, itemId => {}", ownerId, itemId);
        return itemService.updateItem(ownerId, itemId, itemResponseDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long ownerId,
                               @PathVariable long itemId) {
        log.info("deleteItemById ownerId => {}, itemId => {}", ownerId, itemId);
        itemService.deleteItemById(ownerId, itemId);
    }

    @GetMapping
    public List<ItemWithBookingAndCommentsResponseDto> findItemsWithBookingResponseDtoByOwnerId(
            @RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long ownerId,
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("findItemsWithBookingResponseDtoByOwnerId ownerId => {}", ownerId);
        return itemService.findItemWithBookingAndCommentsResponseDtoByOwnerId(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> findItemIsAvailableByNameOrDescription(
            @RequestParam(value = "text") String text,
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("findItemIsAvailableByNameOrDescription text => {}", text);
        return itemService.findItemIsAvailableByNameOrDescription(text, from, size);
    }


    @PostMapping("/{itemId}/comment")
    @Validated
    public CommentResponseDto createComment(@RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long bookerId,
                                            @PathVariable long itemId,
                                            @RequestBody CommentRequestDto commentRequestDto) {
        log.info("createComment bookerId => {}, itemId => {}", bookerId, itemId);
        return itemService.createComment(bookerId, itemId, commentRequestDto);
    }


}
