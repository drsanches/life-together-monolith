package ru.drsanches.life_together.app.service.validation.annotation;

import ru.drsanches.life_together.app.service.validation.validator.ValidateCollectionValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidateCollectionValidator.class)
public @interface ValidateCollection {

    String message() default "collection contains invalid objects";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}