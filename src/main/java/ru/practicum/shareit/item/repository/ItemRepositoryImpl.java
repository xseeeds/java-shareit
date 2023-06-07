package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import ru.practicum.shareit.expception.exp.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final Map<Long, ItemEntity> items = new HashMap<>();
    private final Map<Long, List<Long>> itemIdsByOwnerId = new HashMap<>();
    private long generatorId = 0;

    @Override
    public ItemDto getItemDtoById(long itemId) throws NotFoundException {

        checkExistItemById(itemId);

        return itemMapper.toItemDto(items.get(itemId));
    }

    @Override
    public ItemDto createItem(long ownerId, ItemDto itemDto) {

        userRepository.checkExistUserById(ownerId);

        final ItemEntity createdItemEntity = itemMapper.toItem(itemDto, ownerId)
                .toBuilder()
                .id(getNextGeneratorId())
                .build();

        items.put(createdItemEntity.getId(), createdItemEntity);

        itemIdsByOwnerId.computeIfAbsent(
                        ownerId,
                        v -> new ArrayList<>())
                .add(createdItemEntity.getId());

        return itemMapper.toItemDto(createdItemEntity);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) throws NotFoundException {

        this.checkExistItemByUserId(userId, itemId);

        ItemEntity updatedItemEntity = items.get(itemId);

        if (itemDto.getName() != null) {
            updatedItemEntity = updatedItemEntity.toBuilder().name(itemDto.getName()).build();
        }
        if (itemDto.getDescription() != null) {
            updatedItemEntity = updatedItemEntity.toBuilder().description(itemDto.getDescription()).build();
        }
        if (itemDto.getAvailable() != null) {
            updatedItemEntity = updatedItemEntity.toBuilder().available(itemDto.getAvailable()).build();
        }

        items.put(updatedItemEntity.getId(), updatedItemEntity);

        return itemMapper.toItemDto(updatedItemEntity);
    }

    @Override
    public void deleteItem(long userId, long itemId) {

        this.checkExistItemByUserId(userId, itemId);

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
            throw new NotFoundException("Вещь по id => " + itemId + " не существует");
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
                .filter(itemEntity -> itemEntity.getAvailable()
                        && (StringUtils.containsIgnoreCase(itemEntity.getName(), text) ||
                        StringUtils.containsIgnoreCase(itemEntity.getDescription(), text)))
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

    @Override
    public List<ItemEntity> search(String text) {
        return null;
    }

    private long getNextGeneratorId() {
        return ++generatorId;
    }
}
