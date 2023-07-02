package ru.practicum.shareit.exception.model;

import lombok.Generated;

import java.util.List;

@Generated
public record ValidationErrorResponse(List<Violation> violations) {

}