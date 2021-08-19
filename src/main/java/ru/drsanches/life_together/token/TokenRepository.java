package ru.drsanches.life_together.token;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface TokenRepository extends CrudRepository<Token, String> {

    Optional<Token> findByRefreshToken(String refreshToken);
}