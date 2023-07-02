package ru.practicum.shareit.item.dto.item;

import lombok.*;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder(toBuilder = true)
@Generated
public class ItemRequestDto {

	@NotNull(groups = Marker.OnCreate.class)
	@NotBlank(groups = Marker.OnCreate.class)
	String name;

	@NotNull(groups = Marker.OnCreate.class)
	@NotBlank(groups = Marker.OnCreate.class)
	String description;

	@NotNull(groups = Marker.OnCreate.class)
	Boolean available;

	Long requestId;

}
