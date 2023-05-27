package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public interface ItemMapper {

    ItemDto toItemDto(Item item);

    Item toItemSetNextIdSetOwner(ItemDtoCreate itemDtoCreate, long ownerId);

}
