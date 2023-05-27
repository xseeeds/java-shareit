package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.expception.exp.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
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

    @Override
    public ItemDto createItem(@Positive long userId, @Valid ItemDtoCreate itemDto) {
        userRepository.checkExistUserById(userId);
        return itemRepository.createItem(userId, itemDto);
    }

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
