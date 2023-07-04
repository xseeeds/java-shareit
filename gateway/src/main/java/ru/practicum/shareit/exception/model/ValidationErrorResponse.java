package ru.practicum.shareit.exception.model;

import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@Generated
@RequiredArgsConstructor
public class ValidationErrorResponse {

    private final List<Violation> violations;

}