package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.expception.exp.ConflictException;
import ru.practicum.shareit.expception.exp.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemEntity;

import java.util.List;

public interface ItemRepository {

    ItemDto getItemDtoById(long itemId) throws NotFoundException;

    ItemDto createItem(long ownerId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto) throws NotFoundException;

    void deleteItem(long userId, long itemId);

    void checkExistItemByUserId(long userId, long itemId) throws ConflictException;

    void checkExistItemById(long itemId) throws NotFoundException, ConflictException;

    List<ItemDto> getItemIsNotRentedByName(String text);

    List<ItemDto> getListItemsDtoByUser(long userId);

    @Query("select i from ItemEntity as i " +
            "where i.name ilike concat('%', ?1, '%') " +
            "or i.description ilike concat('%', ?1, '%')")
    List<ItemEntity> search(String text);

}
