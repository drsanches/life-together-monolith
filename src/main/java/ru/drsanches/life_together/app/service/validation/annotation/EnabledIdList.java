package ru.drsanches.life_together.app.service.validation.annotation;

import ru.drsanches.life_together.app.service.validation.validator.EnabledIdListValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnabledIdListValidator.class)
public @interface EnabledIdList {

    String message() default "some user ids do not exist or users have been deleted";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}