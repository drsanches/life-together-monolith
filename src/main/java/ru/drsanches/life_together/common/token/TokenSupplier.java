package ru.drsanches.life_together.common.token;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import ru.drsanches.life_together.common.token.data.Token;

@Component
@RequestScope
public class TokenSupplier {

    private Token token = null;

    public Token get() {
        return token;
    }

    void set(Token token) {
        this.token = token;
    }
}