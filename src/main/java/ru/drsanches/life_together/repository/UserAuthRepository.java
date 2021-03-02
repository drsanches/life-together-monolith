package ru.drsanches.life_together.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.drsanches.life_together.data.auth.model.UserAuth;
import java.util.Optional;

@Repository
public interface UserAuthRepository extends CrudRepository<UserAuth, String> {

    Optional<UserAuth> findByUsername(String username);
}