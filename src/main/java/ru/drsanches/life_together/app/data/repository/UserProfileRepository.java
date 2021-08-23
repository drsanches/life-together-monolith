package ru.drsanches.life_together.app.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.drsanches.life_together.app.data.profile.model.UserProfile;

@Repository
public interface UserProfileRepository extends CrudRepository<UserProfile, String> {}