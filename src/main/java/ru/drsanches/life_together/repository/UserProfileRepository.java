package ru.drsanches.life_together.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.drsanches.life_together.data.user.profile.UserProfile;

@Repository
public interface UserProfileRepository extends CrudRepository<UserProfile, String> {}