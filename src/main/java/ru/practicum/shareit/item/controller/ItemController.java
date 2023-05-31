package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(X_SHARER_USER_ID) long userId, @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId, @RequestHeader(X_SHARER_USER_ID) long userId, @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemDtoById(@PathVariable long itemId) {
        return itemService.getItemDtoById(itemId);
    }

    @GetMapping
    public List<ItemDto> getListItemsDtoByUser(@RequestHeader(X_SHARER_USER_ID) long userId) {
        return itemService.getListItemsDtoByUser(userId);
    }

    @GetMapping("/search") //?text={text}
    public List<ItemDto> getItemIsNotRentedByNameOrDescription(@RequestParam(value = "text") String text,
                                                               @RequestHeader(X_SHARER_USER_ID) long userId) {
        return itemService.getItemIsNotRentedByNameOrDescription(text, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@RequestHeader(X_SHARER_USER_ID) long userId,
                           @PathVariable long itemId) {
        itemService.deleteItem(userId, itemId);
    }
}
