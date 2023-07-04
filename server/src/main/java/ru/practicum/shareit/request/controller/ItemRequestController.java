package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.header.HttpHeadersShareIt.X_SHARER_USER_ID;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestResponseDto createItemRequest(@RequestHeader(X_SHARER_USER_ID) long requesterId,
                                                    @RequestBody ItemRequestRequestDto itemRequestDto) {
        log.info("SERVER => createItemRequest requesterId => {}", requesterId);
        return itemRequestService.createItemRequest(requesterId, itemRequestDto);
    }

    @GetMapping("/{itemRequestId}")
    public ItemRequestResponseDto findItemRequestById(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                      @PathVariable long itemRequestId) {
        log.info("SERVER => findItemRequestById userId => {}, itemRequestId => {}", userId, itemRequestId);
        return itemRequestService.findItemRequestById(itemRequestId, userId);
    }

    @GetMapping
    public List<ItemRequestResponseDto> findOwnerItemRequest(@RequestHeader(X_SHARER_USER_ID) long requesterId,
                                                             @RequestParam(value = "from", defaultValue = "0") int from,
                                                             @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("SERVER => findOwnerItemRequest requesterId => {}", requesterId);
        return itemRequestService.findOwnerItemRequest(requesterId, from, size);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> findOthersItemRequest(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                              @RequestParam(value = "from", defaultValue = "0") int from,
                                                              @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("SERVER => findOthersItemRequest userId => {}", userId);
        return itemRequestService.findOthersItemRequest(userId, from, size);
    }

}
