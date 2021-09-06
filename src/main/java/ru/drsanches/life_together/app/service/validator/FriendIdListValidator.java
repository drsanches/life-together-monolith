package ru.drsanches.life_together.app.service.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.drsanches.life_together.app.service.domain.FriendsDomainService;
import ru.drsanches.life_together.common.token.TokenSupplier;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Set;

@Component
public class FriendIdListValidator implements ConstraintValidator<FriendIdList, Set<String>> {

    private boolean mayContainCurrent;

    @Autowired
    private FriendsDomainService friendsDomainService;

    @Autowired
    private TokenSupplier tokenSupplier;

    @Override
    public void initialize(FriendIdList constraintAnnotation) {
        this.mayContainCurrent = constraintAnnotation.mayContainCurrent();
    }

    @Override
    public boolean isValid(Set<String> userIds, ConstraintValidatorContext context) {
        String currentUserId = tokenSupplier.get().getUserId();
        if (currentUserId == null || userIds == null) {
            return true;
        }
        List<String> friendIdList = friendsDomainService.getFriendsIdList(currentUserId);
        if (mayContainCurrent) {
            friendIdList.add(currentUserId);
        }
        return friendIdList.containsAll(userIds);
    }
}