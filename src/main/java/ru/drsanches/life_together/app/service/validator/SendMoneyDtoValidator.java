package ru.drsanches.life_together.app.service.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.drsanches.life_together.app.data.debts.dto.SendMoneyDTO;
import ru.drsanches.life_together.app.data.profile.model.UserProfile;
import ru.drsanches.life_together.app.service.domain.FriendsDomainService;
import ru.drsanches.life_together.app.service.domain.UserProfileDomainService;
import ru.drsanches.life_together.exception.application.ApplicationException;
import ru.drsanches.life_together.exception.application.WrongRecipientsException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SendMoneyDtoValidator {

    @Autowired
    private UserProfileDomainService userProfileDomainService;

    @Autowired
    private FriendsDomainService friendsDomainService;

    public void validate(String fromUserId, SendMoneyDTO sendMoneyDTO) {
        if (sendMoneyDTO.getMoney() == null || sendMoneyDTO.getMoney() <= 0) {
            throw new ApplicationException("Money must be positive: money=" + sendMoneyDTO.getMoney());
        }
        if (sendMoneyDTO.getToUserIds() == null || sendMoneyDTO.getToUserIds().isEmpty()) {
            throw new ApplicationException("User id list is empty");
        }
        List<String> wrongIds = getWrongIds(fromUserId, sendMoneyDTO.getToUserIds());
        if (!wrongIds.isEmpty()) {
            throw new WrongRecipientsException(wrongIds);
        }
    }

    private List<String> getWrongIds(String fromUserId, Set<String> toUserIds) {
        List<String> toFriends = friendsDomainService.getFriendsIdList(fromUserId);
        List<String> existingFriends = userProfileDomainService.getAllByIds(toFriends).stream()
                .filter(UserProfile::isEnabled)
                .map(UserProfile::getId)
                .collect(Collectors.toList());
        return toUserIds.stream()
                .filter(id -> !existingFriends.contains(id) && !id.equals(fromUserId))
                .collect(Collectors.toList());
    }
}