package ru.drsanches.life_together.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.auth.data.enumeration.Role;
import ru.drsanches.life_together.auth.data.model.UserAuth;
import ru.drsanches.life_together.auth.service.UserAuthDomainService;
import ru.drsanches.life_together.exception.application.NoUserIdException;

@Service
public class UserPermissionService {

    @Autowired
    private UserAuthDomainService userAuthDomainService;

    public boolean isAdmin(String userId) {
        UserAuth userAuth;
        try {
            userAuth = userAuthDomainService.getEnabledById(userId);
        } catch (NoUserIdException e) {
            return false;
        }
        return Role.ADMIN.equals(userAuth.getRole());
    }
}