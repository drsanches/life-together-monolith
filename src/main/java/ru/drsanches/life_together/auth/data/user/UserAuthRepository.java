package ru.drsanches.life_together.auth.data.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserAuthRepository extends CrudRepository<UserAuth, String> {

    Optional<UserAuth> findByUsername(String username);
}