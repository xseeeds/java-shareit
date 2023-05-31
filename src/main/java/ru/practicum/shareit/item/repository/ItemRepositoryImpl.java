package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import ru.practicum.shareit.expception.exp.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Long>> itemIdsByOwnerId = new HashMap<>();
    private long generatorId = 0;

    @Override
    public ItemDto getItemDtoById(long itemId) throws NotFoundException {

        checkExistItemById(itemId);

        return itemMapper.toItemDto(items.get(itemId));
    }

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {

        userRepository.checkExistUserById(userId);

        final Item createdItem = itemMapper
                .toItem(itemDto)
                .toBuilder()
                .id(getNextGeneratorId())
                .owner(userId)
                .build();

        items.put(createdItem.getId(), createdItem);

        itemIdsByOwnerId.computeIfAbsent(
                        userId,
                        v -> new ArrayList<>())
                .add(createdItem.getId());

        return itemMapper.toItemDto(createdItem);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) throws NotFoundException {

        checkExistItemByUserId(userId, itemId);

        Item updatedItem = items.get(itemId);

        if (itemDto.getName() != null) {
            updatedItem = updatedItem.toBuilder().name(itemDto.getName()).build();
        }
        if (itemDto.getDescription() != null) {
            updatedItem = updatedItem.toBuilder().description(itemDto.getDescription()).build();
        }
        if (itemDto.getAvailable() != null) {
            updatedItem = updatedItem.toBuilder().available(itemDto.getAvailable()).build();
        }

        items.put(updatedItem.getId(), updatedItem);

        return itemMapper.toItemDto(updatedItem);
    }

    @Override
    public void deleteItem(long userId, long itemId) {

        checkExistItemByUserId(userId, itemId);

        items.remove(itemId);

        itemIdsByOwnerId.get(userId).remove(itemId);
    }

    @Override
    public void checkExistItemByUserId(long userId, long itemId) throws NotFoundException {

        if (itemIdsByOwnerId.get(userId) == null || !itemIdsByOwnerId.get(userId).contains(itemId)) {
            throw new NotFoundException("У пользователя с id => " + userId + " не существует вещь по id => " + itemId);
        }
    }

    @Override
    public void checkExistItemById(long itemId) throws NotFoundException {

        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Вещь по id => " + itemId + " не найдена");
        }
    }

    @Override
    public List<ItemDto> getItemIsNotRentedByName(String text) {

        if (text.isBlank()) {
            return List.of();
        }

        return items
                .values()
                .stream()
                .filter(item -> item.getAvailable()
                        && (StringUtils.containsIgnoreCase(item.getName(), text) ||
                        StringUtils.containsIgnoreCase(item.getDescription(), text)))
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getListItemsDtoByUser(long userId) {

        return itemIdsByOwnerId
                .get(userId)
                .stream()
                .map(items::get)
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private long getNextGeneratorId() {
        return ++generatorId;
    }
}
