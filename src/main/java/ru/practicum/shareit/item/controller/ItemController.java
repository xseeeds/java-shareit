package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.header.HttpHeadersShareIt;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long userId, @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId, @RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long userId, @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemDtoById(@PathVariable long itemId) {
        return itemService.getItemDtoById(itemId);
    }

    @GetMapping
    public List<ItemDto> getListItemsDtoByUser(@RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long userId) {
        return itemService.getListItemsDtoByUser(userId);
    }

    @GetMapping("/search") //?text={text}
    public List<ItemDto> getItemIsNotRentedByNameOrDescription(@RequestParam(value = "text") String text,
                                                               @RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long userId) {
        return itemService.getItemIsNotRentedByNameOrDescription(text, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@RequestHeader(HttpHeadersShareIt.X_SHARER_USER_ID) long userId,
                           @PathVariable long itemId) {
        itemService.deleteItem(userId, itemId);
    }
}
