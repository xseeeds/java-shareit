package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.expception.exp.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validation.Marker;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    final ItemRepository itemRepository;
    final UserRepository userRepository;

    @Override
    public ItemDto getItemDtoById(@Positive long itemId) throws NotFoundException {
        return itemRepository.getItemDtoById(itemId);
    }

    @Override
    public List<ItemDto> getItemIsNotRentedByNameOrDescription(@NotNull String text, @Positive long userId) {
        return itemRepository.getItemIsNotRentedByName(text);
    }

    @Validated(Marker.OnCreate.class)
    @Override
    public ItemDto createItem(@Positive long userId, @Valid ItemDto itemDto) {
        return itemRepository.createItem(userId, itemDto);
    }

    @Validated(Marker.OnUpdate.class)
    @Override
    public ItemDto updateItem(@Positive long userId, @Positive long itemId, @Valid ItemDto itemDto) throws NotFoundException {
        return itemRepository.updateItem(userId, itemId, itemDto);
    }

    @Override
    public void deleteItem(@Positive long userId, @Positive long itemId) {
        itemRepository.deleteItem(userId, itemId);
    }

    @Override
    public List<ItemDto> getListItemsDtoByUser(@Positive long userId) {
        return itemRepository.getListItemsDtoByUser(userId);
    }

}
