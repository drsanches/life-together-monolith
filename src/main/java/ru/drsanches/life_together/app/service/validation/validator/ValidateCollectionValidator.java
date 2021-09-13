package ru.drsanches.life_together.app.service.validation.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.drsanches.life_together.app.service.validation.annotation.ValidateCollection;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
public class ValidateCollectionValidator implements ConstraintValidator<ValidateCollection, Collection<?>> {

    @Autowired
    private Validator validator;

    @Override
    public boolean isValid(Collection<?> objects, ConstraintValidatorContext context) {
        Set<ConstraintViolation<?>> errors = new HashSet<>();
        if (objects == null) {
            return true;
        }
        for (Object object : objects) {
            errors.addAll(validator.validate(object));
        }
        //TODO: form custom message for each case
        return errors.isEmpty();
    }
}