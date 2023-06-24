package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequestEntity;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class ItemRequestMapper {

    public ItemRequestEntity toItemRequestEntityAndCreatedNow(ItemRequestDto itemRequestDto, long requesterId) {
        return ItemRequestEntity
                .builder()
                .description(itemRequestDto.getDescription())
                .created(LocalDateTime.now())
                .requesterId(requesterId)
                .build();
    }

    public ItemRequestResponseDto toItemRequestResponseDto(ItemRequestEntity itemRequestEntity, List<ItemShortResponseDto> items) {
        return ItemRequestResponseDto
                .builder()
                .id(itemRequestEntity.getId())
                .requester(UserResponseDto
                        .builder()
                        .id(itemRequestEntity.getRequesterId())
                        .name(itemRequestEntity.getRequester() != null ? itemRequestEntity.getRequester().getName() : null)
                        .email(itemRequestEntity.getRequester() != null ? itemRequestEntity.getRequester().getEmail() : null)
                        .build())
                .description(itemRequestEntity.getDescription())
                .created(itemRequestEntity.getCreated())
                .items(items)
                .build();
    }
}
