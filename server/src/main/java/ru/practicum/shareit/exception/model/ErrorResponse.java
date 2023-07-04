package ru.practicum.shareit.exception.model;

import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Generated
public class ErrorResponse {

    private final LocalDateTime timestamp = LocalDateTime.now();

    private final String status;

    private final String error;

    private final String description;
}