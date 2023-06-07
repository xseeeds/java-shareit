package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemEntity;

@Component
public class ItemMapperImpl implements ItemMapper {

    @Override
    public ItemDto toItemDto(ItemEntity itemEntity) {
        return ItemDto
                .builder()
                .id(itemEntity.getId())
                .name(itemEntity.getName())
                .description(itemEntity.getDescription())
                .available(itemEntity.getAvailable())
                .build();
    }

    @Override
    public ItemEntity toItem(ItemDto itemDto, long ownerId) {
        return ItemEntity
                .builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .ownerId(ownerId)
                .build();
    }

}
