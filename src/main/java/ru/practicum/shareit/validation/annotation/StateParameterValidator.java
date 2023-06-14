package ru.practicum.shareit.validation.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StateParameterValidator implements ConstraintValidator<ValidStateParameter, String> {

    @Override
    public void initialize(ValidStateParameter constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String state, ConstraintValidatorContext constraintValidatorContext) {
        return state.equalsIgnoreCase("ALL")
                || state.equalsIgnoreCase("CURRENT")
                || state.equalsIgnoreCase("PAST")
                || state.equalsIgnoreCase("REJECTED")
                || state.equalsIgnoreCase("WAITING")
                || state.equalsIgnoreCase("FUTURE");
    }
}