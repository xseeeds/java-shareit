package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.expception.exp.ConflictException;
import ru.practicum.shareit.expception.exp.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemRepository {

    ItemDto getItemDtoById(long itemId) throws NotFoundException;

    ItemDto createItem(long userId, ItemDtoCreate itemDtoCreate);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto) throws NotFoundException;

    void deleteItem(long userId, long itemId);

    void checkExistItemByUserId(long userId, long itemId) throws ConflictException;

    void checkExistItemById(long itemId) throws NotFoundException, ConflictException;

    List<ItemDto> getItemIsNotRentedByName(String text);

    List<ItemDto> getListItemsDtoByUser(long userId);
}
