package ru.practicum.shareit.validation.annotation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StateParameterValidator.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidStateParameter {

    String message() default "Поле State должно быть => {ALL, CURRENT, PAST, REJECTED, WAITING, FUTURE}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
