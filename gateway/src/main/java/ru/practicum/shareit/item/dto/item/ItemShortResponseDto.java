package ru.practicum.shareit.item.dto.item;


import lombok.*;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Generated
public class ItemShortResponseDto {

	Long id;

	String name;

	String description;

	Boolean available;

	Long requestId;

}