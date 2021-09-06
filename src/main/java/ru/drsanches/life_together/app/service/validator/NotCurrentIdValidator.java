package ru.drsanches.life_together.app.service.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.drsanches.life_together.common.token.TokenSupplier;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class NotCurrentIdValidator implements ConstraintValidator<NotCurrentId, String> {

    @Autowired
    private TokenSupplier tokenSupplier;

    @Override
    public boolean isValid(String userId, ConstraintValidatorContext context) {
        return tokenSupplier.get() == null || !tokenSupplier.get().getUserId().equals(userId);
    }
}