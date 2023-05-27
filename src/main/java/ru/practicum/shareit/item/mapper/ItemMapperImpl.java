package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapperImpl implements ItemMapper {

    private long generatorId = 0;

    @Override
    public ItemDto toItemDto(Item item) {
        return ItemDto
                .builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    @Override
    public Item toItemSetNextIdSetOwner(ItemDtoCreate itemDtoCreate, long ownerId) {
        return Item
                .builder()
                .id(getNextGeneratorId())
                .name(itemDtoCreate.getName())
                .description(itemDtoCreate.getDescription())
                .available(itemDtoCreate.getAvailable())
                .owner(ownerId)
                .build();
    }

    private long getNextGeneratorId() {
        return ++generatorId;
    }
}
