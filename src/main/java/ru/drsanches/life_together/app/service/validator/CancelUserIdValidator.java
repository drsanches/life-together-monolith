package ru.drsanches.life_together.app.service.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.drsanches.life_together.app.service.domain.UserProfileDomainService;
import ru.drsanches.life_together.exception.application.ApplicationException;
import ru.drsanches.life_together.exception.application.NoUserIdException;

@Component
public class CancelUserIdValidator {

    @Autowired
    private UserProfileDomainService userProfileDomainService;

    public void validate(String currentUserId, String cancelUserId) {
        if (!userProfileDomainService.anyExistsById(cancelUserId)) {
            throw new NoUserIdException(cancelUserId);
        }
        if (currentUserId.equals(cancelUserId)) {
            throw new ApplicationException("You can't cancel debt for yourself");
        }
    }
}