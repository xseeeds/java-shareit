package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
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
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemResponseDto createItem(@RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long ownerId,
                                      @RequestBody ItemResponseDto itemResponseDto) {
        return itemService.createItem(ownerId, itemResponseDto);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingAndCommentsResponseDto findItemDtoById(
            @RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long userId,
            @PathVariable long itemId,
            @RequestParam(value = "from", required = false, defaultValue = "0") int from,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return itemService.findItemWithBookingAndCommentsResponseDtoById(userId, itemId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(@RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long ownerId,
                                      @PathVariable long itemId,
                                      @RequestBody ItemResponseDto itemResponseDto) {
        return itemService.updateItem(ownerId, itemId, itemResponseDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long ownerId,
                               @PathVariable long itemId) {
        itemService.deleteItemById(ownerId, itemId);
    }

    @GetMapping
    public List<ItemWithBookingAndCommentsResponseDto> findItemsWithBookingResponseDtoByOwnerId(
            @RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long ownerId,
            @RequestParam(value = "from", required = false, defaultValue = "0") int from,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return itemService.findItemWithBookingAndCommentsResponseDtoByOwnerId(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> findItemIsAvailableByNameOrDescription(
            @RequestParam(value = "text") String text,
            @RequestParam(value = "from", required = false, defaultValue = "0") int from,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return itemService.findItemIsAvailableByNameOrDescription(text, from, size);
    }


    @PostMapping("/{itemId}/comment")
    @Validated
    public CommentResponseDto createComment(@RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long bookerId,
                                            @PathVariable long itemId,
                                            @RequestBody CommentRequestDto commentRequestDto) {
        return itemService.createComment(bookerId, itemId, commentRequestDto);
    }


}
