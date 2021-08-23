package ru.drsanches.life_together.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.auth.data.enumeration.Role;
import ru.drsanches.life_together.auth.data.model.UserAuth;
import ru.drsanches.life_together.auth.data.repository.UserAuthRepository;
import java.util.Optional;

@Service
public class UserPermissionService {

    @Autowired
    UserAuthRepository userAuthRepository;

    public boolean isAdmin(String userId) {
        Optional<UserAuth> user = userAuthRepository.findById(userId);
        if (user.isEmpty() || !user.get().isEnabled()) {
            return false;
        }
        return Role.ADMIN.equals(user.get().getRole());
    }
}