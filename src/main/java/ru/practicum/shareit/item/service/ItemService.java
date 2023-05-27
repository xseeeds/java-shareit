package ru.practicum.shareit.item.service;

import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.dto.ItemDto;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Validated
public interface ItemService {

    ItemDto getItemDtoById(@Positive long itemId);

    List<ItemDto> getItemIsNotRentedByNameOrDescription(@NotNull String text, @Positive long userId);

    @Validated
    ItemDto createItem(@Positive long userId, @Valid ItemDtoCreate itemDto);

    @Validated
    ItemDto updateItem(@Positive long userId, @Positive long itemId, @Valid ItemDto itemDto);

    List<ItemDto> getListItemsDtoByUser(@Positive long userId);

    void deleteItem(@Positive long itemId, @Positive long userId);

}
