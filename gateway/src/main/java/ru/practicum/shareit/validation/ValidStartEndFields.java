package ru.practicum.shareit.validation;

import lombok.Generated;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StartEndFieldsValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Generated
public @interface ValidStartEndFields {

    String message() default "startTime не должно быть в прошлом и endTime не должно быть раньше startTime";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
