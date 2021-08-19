package ru.drsanches.life_together.service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.data.auth.enumeration.Role;
import ru.drsanches.life_together.data.auth.model.UserAuth;
import ru.drsanches.life_together.exception.AuthException;
import ru.drsanches.life_together.repository.UserAuthRepository;
import java.util.Optional;

@Service
public class UserPermissionService {

    @Autowired
    UserAuthRepository userAuthRepository;

    public boolean isAdmin(String userId) {
        Optional<UserAuth> user = userAuthRepository.findById(userId);
        if (user.isEmpty() || !user.get().isEnabled()) {
            throw new AuthException();
        }
        return Role.ADMIN.equals(user.get().getRole());
    }
}