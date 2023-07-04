package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Generated
public class ItemRequestRequestDto {

    @NotNull
    @NotBlank
    String description;

}

