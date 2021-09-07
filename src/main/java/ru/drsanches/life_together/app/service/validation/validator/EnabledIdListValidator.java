package ru.drsanches.life_together.app.service.validation.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.drsanches.life_together.app.data.profile.model.UserProfile;
import ru.drsanches.life_together.app.service.domain.UserProfileDomainService;
import ru.drsanches.life_together.app.service.validation.annotation.EnabledIdList;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

@Component
public class EnabledIdListValidator implements ConstraintValidator<EnabledIdList, Set<String>> {

    @Autowired
    private UserProfileDomainService userProfileDomainService;

    @Override
    public boolean isValid(Set<String> userIds, ConstraintValidatorContext context) {
        if (userIds == null) {
            return true;
        }
        return userProfileDomainService.getAllByIds(userIds).stream()
                .filter(UserProfile::isEnabled)
                .count() == userIds.size();
    }
}