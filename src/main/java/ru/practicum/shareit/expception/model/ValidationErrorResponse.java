package ru.practicum.shareit.expception.model;

import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
@Generated
public class ValidationErrorResponse {

    private final List<Violation> violations;

}