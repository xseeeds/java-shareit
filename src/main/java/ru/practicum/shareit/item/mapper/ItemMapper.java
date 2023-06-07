package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemEntity;

public interface ItemMapper {

    ItemDto toItemDto(ItemEntity itemEntity);

    ItemEntity toItem(ItemDto itemDto, long ownerId);

}
