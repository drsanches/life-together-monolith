package ru.drsanches.life_together.app.service.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.app.data.profile.model.UserProfile;
import ru.drsanches.life_together.app.data.profile.repository.UserProfileRepository;
import ru.drsanches.life_together.exception.application.NoUserIdException;
import ru.drsanches.life_together.exception.application.NoUsernameException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class UserProfileDomainService {

    private final Logger LOG = LoggerFactory.getLogger(UserProfileDomainService.class);

    @Autowired
    private UserProfileRepository userProfileRepository;

    public void save(UserProfile userProfile) {
        userProfileRepository.save(userProfile);
        LOG.info("UserProfile has been updated: {}", userProfile);
    }

    public UserProfile getEnabledById(String userId) {
        Optional<UserProfile> userProfile = userProfileRepository.findById(userId);
        if (userProfile.isEmpty() || !userProfile.get().isEnabled()) {
            throw new NoUserIdException(userId);
        }
        return userProfile.get();
    }

    public UserProfile getEnabledByUsername(String username) {
        Optional<UserProfile> userProfile = userProfileRepository.findByUsername(username);
        if (userProfile.isEmpty() || !userProfile.get().isEnabled()) {
            throw new NoUsernameException(username);
        }
        return userProfile.get();
    }

    public boolean enabledExistsById(String userId) {
        Optional<UserProfile> userProfile = userProfileRepository.findById(userId);
        return userProfile.isPresent() && userProfile.get().isEnabled();
    }

    public boolean anyExistsById(String userId) {
        return userProfileRepository.existsById(userId);
    }

    public List<UserProfile> getAllByIds(Collection<String> userIds) {
        List<UserProfile> profiles = new LinkedList<>();
        userProfileRepository.findAllById(userIds).forEach(profiles::add);
        return profiles;
    }
}