package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.header.HttpHeadersShareIt;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestResponseDto createItemRequest(@RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long requesterId,
                                                    @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.createItemRequest(requesterId, itemRequestDto);
    }

    @GetMapping("/{itemRequestId}")
    public ItemRequestResponseDto findItemRequestById(@RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long userId,
                                                      @PathVariable long itemRequestId) {
        return itemRequestService.findItemRequestById(itemRequestId, userId);
    }

    @GetMapping
    public List<ItemRequestResponseDto> findOwnerItemRequest(
            @RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long requesterId,
            @RequestParam(value = "from", required = false, defaultValue = "0") int from,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return itemRequestService.findOwnerItemRequest(requesterId, from, size);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> findOthersItemRequest(
            @RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long userId,
            @RequestParam(value = "from", required = false, defaultValue = "0") int from,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return itemRequestService.findOthersItemRequest(userId, from, size);
    }

}
